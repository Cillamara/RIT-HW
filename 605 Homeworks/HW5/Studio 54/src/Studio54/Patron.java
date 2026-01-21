//package Studio54;
//
//public class Patron implements Comparable<Patron>{
//    int coolness;
//    boolean regular;
//    String name;
//
//    public Patron(int coolness, boolean regular, String name) {
//        this.coolness = coolness;
//        this.regular = regular;
//        this.name = name;
//    }
//
//    /**
//     * Compares the priority of two patrons. If the patrons are
//     *
//     * @param o the object to be compared.
//     * @return
//     */
//    @Override
//    public int compareTo(Patron o) {
//        int compare = this.coolness - o.coolness;
//        if (compare < 0) {
//            return -1;
//        } else if (compare > 0) {
//            return 1;
//        } else {
//            if (o.regular == this.regular) {return 0;
//            } else if (this.regular) {return 1;
//            } else {return -1;}
//        }
//    }
//}
package Studio_54;

public class Patron implements Comparable<Studio_54.Patron> {
    private final String name;
    private final int coolness;
    private final boolean regular;

    public Patron(String name, int coolness, boolean regular) {
        this.name = name;
        this.coolness = coolness;
        if (coolness < 1 || coolness > 10)
            throw new AssertionError("coolness must be between 1 and 10");
        this.regular = regular;
    }

    @Override
    public int compareTo(Studio_54.Patron o) {
        int comparison = this.coolness - o.coolness;
        if (comparison == 0) {
            if (this.regular == o.regular) {
                return 0;
            } else if (!this.regular) {
                return -1;
            } else {
                return 1;
            }
        } else
        if (comparison < 0) {
            return -1;
        } else
            return 1;
    }

    public String getName() {
        return name;
    }

    public int getCoolness() {
        return coolness;
    }

    public boolean isRegular() {
        return regular;
    }

    public String toString() {
        if (regular) {
            return "Regular " + name + " with coolness factor " + coolness;
        } else {
            return name + " with coolness factor " + coolness;
        }
    }

}
