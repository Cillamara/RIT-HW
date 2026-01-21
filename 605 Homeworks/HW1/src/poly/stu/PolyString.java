package poly.stu;

/**
 * This class can return a string representation of a polynomial of order n,
 * in the form:
 * <pre>
 * x^n + x^n-1 + ... x^1 + constant
 * </pre>
 *
 * @author RIT CS
 * @author Liam Cui
 */
public class PolyString {

    /**
     * The displayed variable name
     */
    public final static String VARIABLE_NAME = "x";

    /**
     * Unused constructor, made private to avoid javadoc generation.
     */
    private PolyString() {
    }

    /**
     * Get the string representation of the polynomial.  For example:
     * <pre>
     * poly=[1]: "1"
     * poly=[3, -1]: "-x + 3"
     * poly=[0, 3]: "3x + 0"
     * poly=[2, -1, -2, 1]: "x^3 + -2x^2 + -x + 2"
     * poly=[-5, 0, 0, 3, 3, 1]: "x^5 + 3x^4 + 3x^3 + -5"
     * </pre>
     *
     * @param poly A native array representing the polynomial, in reverse order.
     * @return A string representation of the polynomial.
     * @rit.pre poly is not an empty array.  Minimally it will contain
     * a constant term.
     */
    public static String getString(int[] poly) {
        StringBuilder sb = new StringBuilder();
        for (int i = poly.length - 1; i >= 0; i--) {
            if (i == 0) {
                sb.append(poly[i]);
            } else if (poly[i] != 0) {
                if (i == 1 && poly[i] == - 1 ) {
                    sb.append("-x"); //prints -x instead of -1x
                } else if (i == 1 && poly[i] == 1) {
                    sb.append("x"); //prints x instead of 1x
                } else if (i == 1 && poly[i] != 1 && (poly[i] != -1)) {
                    sb.append(poly[i]).append("x");
                } else if (i > 1) {
                    sb.append(poly[i]).append("x^").append(i);
                }
            }
            if (poly[i] != 0 && i > 0) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }
}