package csp;

import java.nio.MappedByteBuffer;
import java.time.LocalDate;
import java.util.*;


/**
 * @author Tommy Kelly
 * @date April 26th 2020
 */
/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some set of unary and binary 
 * constraints on the dates of each meeting.
 */
public class CSP {
    public static boolean debug = false;
    /**
     * MeetDetail
     * @author tommy
     * Keeps track of a meeting number and its valid dates. 
     */
    static class MeetDetail {
        
        LinkedList<LocalDate> validDates = new LinkedList<LocalDate>();
        
        MeetDetail(){}
        /**
         * addDate : adds date to validDates list
         * @param newDate: date to be added. 
         */
        private void addDate(LocalDate newDate) {
            validDates.add(newDate);
        }

    }
    /**
     * Arc: keeps track of binary constraints for AC3 processing. 
     * @author tommy
     *
     */
    static class Arc {
        private int tail;
        private int head;
        private int conIndex;
        /**
         * Constructor 
         * @param tail : tail of Arc
         * @param head : head of Arc
         * @param conIndex : index in constraint array
         */
        Arc (int tail, int head, int conIndex){
            this.tail = tail;
            this.head = head;
            this.conIndex = conIndex;
            
        }
    }
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */

    
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        MeetDetail[] meetArr = new MeetDetail[nMeetings];
        DateConstraint[] conArr = new DateConstraint[constraints.size()];
        conArr = constraints.toArray(conArr);
        
        //Node Consistency (limit domains)
        for(int i = 0; i < nMeetings; i ++){
            meetArr[i] = new MeetDetail();
            LocalDate currentDate = rangeStart;
            while(currentDate.isBefore(rangeEnd.plusDays(1))) {
                if(validUn(currentDate,i,conArr)) {   
                    meetArr[i].addDate(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
            if (meetArr[i].validDates.isEmpty()) {
                return null;
            }
        }
        //(ARC processing, limit domains based on binary)
        meetArr = AC3(meetArr.clone(),conArr.clone());
        for(MeetDetail i : meetArr) {
            if(i.validDates.isEmpty()) {
                return null;
            }
        }
        return recursiveBacktrack(meetArr, conArr, new LocalDate[nMeetings]);
    }

    /**
     * recursiveBackTrack
     * @param meetArr - the array of meetings with domains. Index is the meeting number
     * @param con - constraint array
     * @param assign - assignment array
     * @return a list of dates or null if no solution exists.
     */
    public static List<LocalDate> recursiveBacktrack(MeetDetail[] meetArr, DateConstraint[] con, LocalDate[] assign ){
        if(isFull(assign)) {
            return Arrays.asList(assign);
        }
        int index = notNull(assign);
        for(LocalDate value : meetArr[index].validDates) {
            assign[index] = value;

            if(testBi(index,assign,con)) {
                List<LocalDate> result = recursiveBacktrack(meetArr,con, assign);
                if(result != null) {
                    return result;
                }
            }
            assign[index] = null;
        }
        return null;
    }
    
    /**
     * AC3 reduces domains of meetings based on binary constraints. 
     * @param array : array of meeting domains, index refers to the meeting number
     * @param con : constraint array. 
     * @return an array with domains that have been possibly reduced. 
     */
    public static MeetDetail[] AC3(MeetDetail[] array, DateConstraint[] con) {
        // lets see, 
        Set<Arc> queue = new HashSet<Arc>();
        for(int i = 0; i < con.length; i++) {
            if(con[i].arity() == 2) {
                Arc newArc = new Arc(con[i].L_VAL,((BinaryDateConstraint) con[i]).R_VAL,i);
                queue.add(newArc);
                newArc = new Arc(((BinaryDateConstraint) con[i]).R_VAL,con[i].L_VAL,i);
                queue.add(newArc);
            }
        }
        //queue is full of arcs...
        while(!queue.isEmpty()) {
            Arc current = removeFirst(queue);
            queue.remove(current);
            switch(con[current.conIndex].OP) {
            case("=="):
                for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                    boolean remove = true;
                    for(int j = 0; j < array[current.head].validDates.size(); j++) {
                        if(array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                            remove = false;
                        }
                    }
                    if (remove) {
                        //remove domain and add neighbor arcs.. basically all where tail is the head... 
                        array[current.tail].validDates.remove(i);
                        i--;
                        for(int k = 0; k < con.length; k++) {
                            if (con[k].arity() == 2) {
                                if(con[k].L_VAL == current.tail) {
                                    queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                    queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                }
                            }
                        }
                    }
                }
                break; 
            case("!="):
                for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                    boolean remove = true;
                    for(int j = 0; j < array[current.head].validDates.size(); j++) {
                        if(!array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                            remove = false;
                        }
                    }
                    if (remove) {
                        //remove domain and add neighbor arcs.. basically all where tail is the head... 
                        array[current.tail].validDates.remove(i);
                        i--;
                        for(int k = 0; k < con.length; k++) {
                            if (con[k].arity() == 2) {
                                if(con[k].L_VAL == current.tail) {
                                    queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                    queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                }
                            }
                        }
                    }
                }
                break;
            case(">"):
                 if(current.tail == con[current.conIndex].L_VAL) {
                     for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                         boolean remove = true;
                         for(int j = 0; j < array[current.head].validDates.size(); j++) {
                             if(array[current.tail].validDates.get(i).isAfter(array[current.head].validDates.get(j))) {
                                 remove = false;
                             }
                         }
                         if (remove) {
                             //remove domain and add neighbor arcs.. basically all where tail is the head... 
                             array[current.tail].validDates.remove(i);
                             i--;
                             for(int k = 0; k < con.length; k++) {
                                 if (con[k].arity() == 2) {
                                     if(con[k].L_VAL == current.tail) {
                                         queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                     } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                         queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                     }
                                 }
                             }
                         }
                     }
                 } else {
                     for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                         boolean remove = true;
                         for(int j = 0; j < array[current.head].validDates.size(); j++) {
                             if(array[current.head].validDates.get(j).isAfter(array[current.tail].validDates.get(i))) {
                                 remove = false;
                             }
                         }
                         if (remove) {
                             //remove domain and add neighbor arcs.. basically all where tail is the head... 
                             array[current.tail].validDates.remove(i);
                             i--;
                             for(int k = 0; k < con.length; k++) {
                                 if (con[k].arity() == 2) {
                                     if(con[k].L_VAL == current.tail) {
                                         queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                     } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                         queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                     }
                                 }
                             }
                         }
                     }
                 }
                 break;
            case("<"):
                if(current.tail == con[current.conIndex].L_VAL) {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.tail].validDates.get(i).isBefore(array[current.head].validDates.get(j))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.head].validDates.get(j).isBefore(array[current.tail].validDates.get(i))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case(">="):
                if(current.tail == con[current.conIndex].L_VAL) {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.tail].validDates.get(i).isAfter(array[current.head].validDates.get(j)) || array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.head].validDates.get(j).isAfter(array[current.tail].validDates.get(i))|| array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case("<="):
                if(current.tail == con[current.conIndex].L_VAL) {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.tail].validDates.get(i).isBefore(array[current.head].validDates.get(j)) || array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for(int i = 0; i < array[current.tail].validDates.size(); i++) {
                        boolean remove = true;
                        for(int j = 0; j < array[current.head].validDates.size(); j++) {
                            if(array[current.head].validDates.get(j).isBefore(array[current.tail].validDates.get(i))|| array[current.tail].validDates.get(i).isEqual(array[current.head].validDates.get(j))) {
                                remove = false;
                            }
                        }
                        if (remove) {
                            //remove domain and add neighbor arcs.. basically all where tail is the head... 
                            array[current.tail].validDates.remove(i);
                            i--;
                            for(int k = 0; k < con.length; k++) {
                                if (con[k].arity() == 2) {
                                    if(con[k].L_VAL == current.tail) {
                                        queue.add(new Arc( ((BinaryDateConstraint)con[k]).R_VAL,current.tail,k));
                                    } else if(((BinaryDateConstraint) con[k]).R_VAL == current.tail) {
                                        queue.add(new Arc(con[k].L_VAL,current.tail,k));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        
        return array;
    }
    /**
     * removeFirst: gets the first pair out of the set queue
     * @param queue
     * @return The arc that is returned
     */
    public static Arc removeFirst(Set<Arc> queue) {
        if(queue.isEmpty()) {
            return null;
        }
        for(Arc pair : queue) {
            return pair;
        }
        return null;
    }
    
    /**
     * testBi: tests binary constraints for a given assignment.
     * @param meeting the index of the meeting to be tested.
     * @param dates the assignment of dates, index is the meeting number
     * @param con constraint array
     * @return true if the constraint is satisfied or either of meetings being tested are null. False if constraint fails.
     */
    public static boolean testBi(int meeting ,LocalDate[] dates, DateConstraint[] con) {
        for(int i = 0; i < con.length; i++) {
            if(con[i].arity() == 2) {
                if(con[i].L_VAL == meeting || ((BinaryDateConstraint)con[i]).R_VAL == meeting) {
                    //print("made it to test");
                    int rVal = ((BinaryDateConstraint) con[i]).R_VAL;
                    int lVal = con[i].L_VAL;
                    //print("lval: " + lVal + " rVal: "+ rVal);
                    if(dates[lVal] == null || dates[rVal] == null) {
                        
                    } else {
                        switch(con[i].OP) {
                        case "==": 
                           // print("made it to equals");
                            if(!dates[lVal].isEqual(dates[rVal])) {
                                return false; 
                            }
                            break;
                        case "!=": 
                            if(dates[lVal].isEqual(dates[rVal])) {
                                return false;
                            }
                            break;
                        case ">":
                            if(!dates[lVal].isAfter(dates[rVal])) {
                                return false;
                            }
                            break;
                        case "<":  
                            if(!dates[lVal].isBefore(dates[rVal])) {
                                return false;
                            }
                            break;
                        case ">=": 
                            if(!dates[lVal].isEqual(dates[rVal]) && !dates[lVal].isAfter(dates[rVal])) {
                                return false;
                            }
                            break;
                        case "<=":
                            if(!dates[lVal].isEqual(dates[rVal]) && !dates[lVal].isBefore(dates[rVal])) {
                                return false;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * notNull : finds the first null assignment in the array.
     * @param array: the assignment array
     * @returns the index of the first null value or the end index
     */
    public static int notNull(LocalDate[] array) {
        
        for (int i = 0; i < array.length; i++) {
          if(array[i] == null){
               return i;
         }
       }
        return array.length - 1;
    }
    /** 
     * isFull determines whether the assignment array is full.
     * @param array : the assignment array 
     * @return true if all positions in the array have been assigned, false if not.
     */
    public static boolean isFull(LocalDate[] array) {
        if(array[array.length - 1] == null) {
            return false;
        }
        return true;
    }
    /**
     * Debug method to print the final array of meetings.
     * @param array
     */
    public static void printMeet(LocalDate[] array) {
        for(int i = 0; i < array.length; i++) {
            System.out.println("Meeting " + i + " date: " + array[i]);
        }
    }
    /**
     * validUn: Restricts domains of meetings based on Unary Date Constraints
     * @param date the date to be tested
     * @param meeting the meeting number to be tested
     * @param con constraint array
     * @return true iff date is valid. False if date is not valid. 
     */
    public static boolean validUn(LocalDate date, int meeting, DateConstraint[] con) {
        for(int i = 0; i < con.length; i++) {
            if(con[i].L_VAL == meeting) {
                if(con[i].arity() == 1) {
                    LocalDate rVal = ((UnaryDateConstraint)con[i]).R_VAL;
                    switch(con[i].OP) {
                    case "==": 
                        if(!date.isEqual(rVal)) {
                            return false;
                        }
                        break;
                    case "!=": 
                        if(date.isEqual(rVal)) {
                            return false;
                        }
                        break;
                    case ">": 
                        if(!date.isAfter(rVal)) {
                            return false;
                        }
                        break;
                    case "<":
                        if(!date.isBefore(rVal)) {
                            return false;
                        }
                        break;
                    case ">=":
                        if(!date.isAfter(rVal) && !date.isEqual(rVal)) {
                            return false;
                        }
                        break;
                    case "<=":
                        if(!date.isBefore(rVal) && !date.isEqual(rVal)) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Debug print method
     * @param s object to be printed. 
     */
    public static void print(Object s) {
        if(debug) {
            System.out.println(s);
        }
    }
}
