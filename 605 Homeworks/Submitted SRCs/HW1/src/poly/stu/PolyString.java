package poly.stu;

import java.util.Arrays;

/**
 * This class can return a string representation of a polynomial of order n,
 * in the form:
 * <pre>
 * x^n + x^n-1 + ... x^1 + constant
 * </pre>
 *
 * @author RIT CS
 * @author Tyler Ronnenberg
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
        /*
        We need to convert the integer array "poly" to a string
        StringBuilder class is more efficient than repeated use of + operator
         */
        StringBuilder polyString = new StringBuilder();
        /*
        For loop iterating in reverse order
        constructs polynomial from left-to-right
         */
        for (int i = poly.length - 1; i >= 0; i--) {

            int coeff = poly[i];
            //The power of a term is related to it's position in the array.
            int power = i;

            boolean printPlusSign = true;
            /*
            Print plus sign?
            Printing in front of term
            Leverage relationship between power
            and iterator determining where a term is in the string
            Don't print plus sign at first term or empty terms (coeff = 0)
             */
            if (coeff == 0 && power >= 1){
                printPlusSign = false;
            }
            if (power < poly.length - 1 && printPlusSign) {
                polyString.append(" + ");
            }
            /*
            Print Coeff?
            Only print coefficients when power is 0 (constants)
            or coefficients are >= 2 or <= -2
            Only print '-' if coefficient = -1
            */
            if (power == 0 || coeff >=2 || coeff <= -2) {
                polyString.append(coeff);
            } else if (coeff == -1) {
                polyString.append('-');
            }
            /*
            Print x?
            Printing x on non-constant and nonzero terms
             */
            if (power >= 1 && coeff != 0) {
                polyString.append(VARIABLE_NAME);
            }
            /*
            Print ^power?
            Printing power on nonzero, degree 2 or greater terms
             */
            if (power >= 2 && coeff != 0) {
                polyString.append("^").append(power);
            }
        }
    return polyString.toString();
    }
}
