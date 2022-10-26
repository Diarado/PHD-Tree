package a4;

import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/** A PhDTree is a tree representing people who have received a PhD degree; each node
 *  represents a person, and the edges represent advisor-advisee relationships, since
 *  PhD students almost always have an advisor who mentors them.
 */
public class PhDTree {
    // You will not need to modify these fields. They are used to help test and
    // print out your output

    /** The String that marks the start of children in toString() */
    public static final String START_ADVISEE_DELIMITER = "[";

    /** The String that divides children in toString() */
    public static final String DELIMITER = ", ";

    /** The String that marks the end of children in toString() */
    public static final String END_ADVISEE_DELIMITER = "]";

    /**
     * The Professor at the root of this PhDTree.
     * i.e. the Professor at this node of this PhDTree.
     * All nodes of a PhDTree have a different Professor in them. The names of
     * professors are all distinct: there are no duplicates.
     */
    private Professor prof;

    /**
     * Year in which this node's professor was awarded their PhD.
     */
    private int phdYear;

    /**
     * The advisees of this PhDTree node.
     * Each element of this set is an advisee of the Professor at this
     * node.  It is the empty set if this node is a leaf. The set of
     * PhDTree nodes reachable via advisees forms a tree.
     */
    private SortedSet<PhDTree> advisees;

    /** Returns false or throws an assertion error if
     *  the class invariant is not satisfied. Requires:
     *  assertion checking is enabled.
     */
    private boolean classInv() {
        Set<Professor> seenProfs = new HashSet<>();
        Set<PhDTree> seenNodes = new HashSet<>();
        return classInvTraverse(seenProfs, seenNodes);
    }

    /**
     * Helper method for classInv. Traverses the tree from this node,
     * adding all Professors and nodes seen to the respective seen sets. Things added must
     * not already be in the set; it would imply the data structure is not a tree. Returns false
     * or throws an assertion error if these conditions are not met.
     */
    private boolean classInvTraverse(Set<Professor> seenProfs, Set<PhDTree> seenNodes) {
        assert !seenProfs.contains(prof) : "prof " + prof + " is not unique";
        assert !seenNodes.contains(this) : "node " + this + " is not unique";
        seenProfs.add(prof);
        seenNodes.add(this);
        for (PhDTree child: advisees) {
            if (!child.classInvTraverse(seenProfs, seenNodes)) return false;
        }
        return true;
    }

    /**
     * Creates: a new PhDTree with root Professor p and no children.
     */
    public PhDTree(Professor p, int year) throws IllegalArgumentException {
        assert p != null;
        prof = p;
        phdYear = year;
        advisees = new TreeSet<>((x, y) -> x.prof.compareTo(y.prof));
        assert classInv();
    }

    /** The Professor at the root of this PhDTree. */
    public Professor prof() {
        return prof;
    }

    /** The number of direct advisees of the professor at the root of the PhDTree. */
    public int numAdvisees() {
        // TODO 1
        assert classInv();
        return advisees.size();
    }

    /**
     * Returns the number of nodes in this PhDTree.
     * Note: If this is a leaf, the size is 1 (just the root)
     */
    public int size() {
        // TODO 2
        // This method must be recursive.
        // State whether this is a searching or a counting method: counting method
        assert classInv();
        if(advisees.size() == 0){
            assert classInv();
            return 1;
        }
        int sum = 1;
        for (PhDTree ptree: advisees){
            sum = sum + ptree.size();
        }
        assert classInv();
        return sum;
    }

    /**
     * The maximum depth of this PhDTree,
     * i.e. the longest path from the root to a leaf.
     * Example: If this PhDTree has only one node, returns 0.
     */
    public int maxDepth() {
        // TODO 3
        assert classInv();
        if(advisees.size() == 0){
            assert classInv();
            return 0;
        }
        int length = 1;
        for(PhDTree ptree: advisees){
            int newLength = 1;
            newLength = newLength + ptree.maxDepth();
            if (newLength >= length){
                length = newLength;
            }
        }
        assert classInv();
        return length;

    }

    /**
     * Returns the subtree with p at the root. Throws NotFound
     * if p is not in the tree.
     */
    public PhDTree findTree(Professor p) throws NotFound {
        // TODO 4
        // You will need to use recursion and to catch the NotFound exception.

        assert classInv();
        if (prof.equals(p)){
            return this;
        }
        int i = 0;
        for (PhDTree ptree : advisees) {
            try {
                PhDTree result = ptree.findTree(p);
                assert classInv();
                return result;
            }catch(NotFound exc){
                if (i == numAdvisees() - 1){
                    assert classInv();
                    throw exc;
                }
            }
            i ++;
        }
        assert classInv();
        throw new NotFound();
    }

    /** Returns true if this PhDTree contains a node with Professor p. */
    public boolean contains(Professor p) {
        assert classInv();
        try {
            findTree(p);
            assert classInv();
            return true;
        } catch (NotFound exc) {
            assert classInv();
            return false;
        }
    }
    /**
     * Effect: Extend the tree rooted at Professor p with a new node for
     * the new advisee, Professor a, who received their PhD in the year
     * year.
     * Checks: p is in this PhDTree, and a is not already in this PhDTree.
     */
    public void insert(Professor p, Professor a, int year) {
        // TODO 5
        // This method should not be recursive.
        // Use method findTree(), above, and use no methods that are below.
        // DO NOT traverse the tree twice looking for the same professor
        // --don't duplicate work.

        assert classInv();
        try{
            PhDTree myP = findTree(p);
            try{
                findTree(a);
            }catch(NotFound exc){
                PhDTree newStudent = new PhDTree(a, year);
                myP.advisees.add(newStudent);
            }
        }catch (NotFound exc){
            System.out.println("Not Found Professor " + p.toString());
        }
        assert classInv();
    }

    /**
     * Returns the immediate advisor of p, or throws NotFound if
     * p is not a descendant of the root node of this tree.
     */
    public Professor findAdvisor(Professor p) throws NotFound {
        assert classInv();
        // TODO 6
        for (PhDTree ptree : advisees){
            if (ptree.prof.equals(p)){
                assert classInv();
                return prof;
            }
        }
        int i = 0;
        for (PhDTree ptree : advisees){
            try {
                Professor result = ptree.findAdvisor(p);
                assert classInv();
                return result;
            }catch(NotFound exc){
                if (i == numAdvisees() - 1){
                    assert classInv();
                    throw exc;
                }
            }
            i++;
        }
        assert classInv();
        throw new NotFound();
    }

    /**
     * Returns: The path between "here" (the root of this PhDTree) to
     * professor descendant p. Throws NotFound if there is no such path.
     */
    public List<Professor> findAcademicLineage(Professor p) throws NotFound {
        // TODO 7
        assert classInv();
        LinkedList<Professor> Lineage = new LinkedList<>();
        if (!contains(p)) {
            assert classInv();
            throw new NotFound();
        }
        Lineage.add(prof);
        if (prof.equals(p)){
            return Lineage;
        }
        int i = 0;
        for (PhDTree ptree: advisees){
            try {
                Lineage.addAll(ptree.findAcademicLineage(p));
                assert classInv();
                return Lineage;
            }catch(NotFound exc){
                if (i == numAdvisees() - 1){
                    assert classInv();
                    throw exc;
                }
            }
            i++;
        }
        assert classInv();
        throw new NotFound();
    }

    /**
     * Returns: The professor at the root of the smallest subtree of
     * this PhDTree that contains prof1 and prof2, if such a subtree
     * exists. Otherwise, throws NotFound.
     */
    public Professor commonAncestor(Professor prof1, Professor prof2) throws NotFound {
        // TODO 8
        assert classInv();
        try{
            List<Professor> r1 = findAcademicLineage(prof1);
            List<Professor> r2 = findAcademicLineage(prof2);
            int i = 0;
            int max = 0;
            Professor result = r1.get(0);
            List<Professor> shorter = r1;
            List<Professor> longer = r2;
            if(r2.size() < r1.size()){
                shorter = r2;
                longer = r1;
            }
            for (Professor prof: shorter){
                if(prof.equals(longer.get(i))){
                    if (i >= max){
                        result = prof;
                        max = i;
                    }
                }
                i++;
            }
            assert classInv();
            return result;
        }catch(NotFound exc){
            assert classInv();
            throw exc;
        }
    }

    /**
     * Return a (single line) String representation of this PhDTree.
     * If this PhDTree has no advisees (it is a leaf), return the root's
     * substring.
     * Otherwise, return
     * ... root's substring + START_ADVISEE_DELIMITER + each
     * advisees's toString, separated by DELIMITER, followed by
     * END_ADVISEE_DELIMITER.
     *
     * Thus, for the following tree:
     *
     * <pre>
     * Depth:
     *   0      Maya_Leong
     *            /     \
     *   1 Matthew_Hui  Curran_Muhlberger
     *           /          /         \
     *   2 Amy_Huang    Tomer_Shamir   Andrew_Myers
     *           \
     *   3    David_Gries
     *
     * Maya_Leong.toString() should print:
     * Maya Leong[Matthew Hui[Amy Huang[David Gries]]],Curran Muhlberger[Tomer Shamir,Andrew Myers]]
     *
     * Matthew_Hui.toString() should print:
     * Matthew Hui[Amy Huang[David Gries]]
     *
     * Andrew_Myers.toString() should print:
     * Andrew Myers
     * </pre>
     */
    @Override
    public String toString() {
        if (advisees.isEmpty())
            return prof.toString();
        StringBuilder s = new StringBuilder();
        s.append(prof.toString())
                .append(START_ADVISEE_DELIMITER);
        boolean first = true;
        for (PhDTree dt : advisees) {
            if (!first) s.append(DELIMITER);
            first = false;
            s.append(dt.toString());
        }
        s.append(END_ADVISEE_DELIMITER);
        return s.toString();
    }

    /**
     * Return a verbose (multi-line) string representing this PhDTree with
     * the professors [first name last name - year].
     * Each professor in the tree is on its own line (there are no spaces at the
     * beginning or end of each new line)
     * Each line is terminated by a newline character ('\n').
     */
    public String toStringVerbose() {
        assert classInv();
        StringBuilder s = new StringBuilder();
        // TODO 9
        // Use a StringBuilder to implement this method (Hint: look at toString)
        if (numAdvisees() == 0){
            assert classInv();
            return s.append(prof).append(" - ").append(phdYear).toString();
        }
        s.append(prof);
        s.append(" - ").append(phdYear);
        for(PhDTree ptree: advisees){
            s.append("\n");
            s.append(ptree.toStringVerbose());
        }
        assert classInv();
        return s.toString();
    }
}