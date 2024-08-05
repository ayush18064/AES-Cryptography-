import java.util.Scanner;

class AES {
    public String PlainText;
    public String Key;
    int[][] substitution = {
            {9, 4, 10, 11},
            {13, 1, 8, 5},
            {6, 2, 0, 3},
            {12, 14, 15, 7},
    };
    boolean Rcon[] = {true, false, false, false, false, false, false, false};
    boolean Rcon2[] = {false, false, true, true, false, false, false, false};
    int[][] MixedColumn = {
            {1, 4},
            {4, 1}
    };

    void keyGeneration() {
        Scanner sc = new Scanner(System.in);
        // taking the 16-bit plaintext input
        System.out.println("Enter the 16-bit plaintext: ");
        PlainText = sc.nextLine();
        System.out.println("Enter the 16-bit Key: ");
        Key = sc.nextLine();
    }

    String[] Split(String str) {
        // Assume splitting the string into two halves for example
        int mid = str.length() / 2;
        String resultLeft = str.substring(0, mid);
        String resultRight = str.substring(mid);
        System.out.println(resultLeft);
        System.out.println(resultRight);
        return new String[]{resultLeft, resultRight};
    }

    String booleanArrayToString(boolean[] arr) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : arr) {
            sb.append(b ? '1' : '0');
        }
        return sb.toString();
    }

    boolean[] convertStringToBooleanArray(String str) {
        boolean[] booleanArray = new boolean[str.length()];
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                booleanArray[i] = true;
            } else if (str.charAt(i) == '0') {
                booleanArray[i] = false;
            } else {
                throw new IllegalArgumentException("String contains invalid characters. Only '1' and '0' are allowed.");
            }
        }
        return booleanArray;
    }

    String swapHalves(String str) {
        if (str.length() != 8) {
            throw new IllegalArgumentException("String length must be 8 bits.");
        }
        String firstHalf = str.substring(0, 4);
        String secondHalf = str.substring(4);
        return secondHalf + firstHalf;
    }

    String substitution(String str) {
        int row1 = Integer.parseInt("" + str.charAt(0) + str.charAt(1), 2);
        int col1 = Integer.parseInt("" + str.charAt(2) + str.charAt(3), 2);
        int row2 = Integer.parseInt("" + str.charAt(4) + str.charAt(5), 2);
        int col2 = Integer.parseInt("" + str.charAt(6) + str.charAt(7), 2);

        int firstHalf = substitution[row1][col1];
        int secondHalf = substitution[row2][col2];

        String firstHalf1 = integerToBinaryString(firstHalf, 4);
        String secondHalf2 = integerToBinaryString(secondHalf, 4);
        return firstHalf1 + secondHalf2;
    }

    String integerToBinaryString(int value, int length) {
        return String.format("%" + length + "s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    boolean[] concatenateBooleanArrays(boolean[] arr1, boolean[] arr2) {
        boolean[] result = new boolean[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    String binaryStringToHexString(String binaryStr) {
        int decimal = Integer.parseInt(binaryStr, 2);
        String hexStr = Integer.toHexString(decimal);
        int len = (int) Math.ceil(binaryStr.length() / 4.0);
        return String.format("%" + len + "s", hexStr).replace(' ', '0');
    }

    String swapNibbles(String str) {
        if (str.length() != 16) {
            throw new IllegalArgumentException("String length must be 16 bits.");
        }
        StringBuilder sb = new StringBuilder(str);
        String nibble2 = sb.substring(4, 8);
        String nibble4 = sb.substring(12, 16);

        sb.replace(4, 8, nibble4);
        sb.replace(12, 16, nibble2);

        return sb.toString();
    }

    boolean[] mixedColumnOperation(boolean[] input) {
        boolean[] output = new boolean[16];
        int[][] mix = MixedColumn;

        // Process each nibble (4 bits) in the input
        for (int i = 0; i < 2; i++) {
            int nibble1 = Integer.parseInt("" + (input[i * 8] ? 1 : 0) + (input[i * 8 + 1] ? 1 : 0) + (input[i * 8 + 2] ? 1 : 0) + (input[i * 8 + 3] ? 1 : 0), 2);
            int nibble2 = Integer.parseInt("" + (input[i * 8 + 4] ? 1 : 0) + (input[i * 8 + 5] ? 1 : 0) + (input[i * 8 + 6] ? 1 : 0) + (input[i * 8 + 7] ? 1 : 0), 2);

            int result1 = mix[0][0] * nibble1 ^ mix[0][1] * nibble2;
            int result2 = mix[1][0] * nibble1 ^ mix[1][1] * nibble2;

            String result1Str = integerToBinaryString(result1, 4);
            String result2Str = integerToBinaryString(result2, 4);

            for (int j = 0; j < 4; j++) {
                output[i * 8 + j] = result1Str.charAt(j) == '1';
                output[i * 8 + 4 + j] = result2Str.charAt(j) == '1';
            }
        }

        return output;
    }

    boolean[] xorArrays(boolean[] arr1, boolean[] arr2) {
        boolean[] result = new boolean[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i] ^ arr2[i];
        }
        return result;
    }

    public void encrypt() {
        keyGeneration();

        String[] splitResult = Split(Key);

        boolean[] leftPartKeyConverted = convertStringToBooleanArray(splitResult[0]);
        boolean[] rightPartKeyConverted = convertStringToBooleanArray(splitResult[1]);

        String rotationW1 = swapHalves(splitResult[1]);
        boolean[] subRotationW1 = convertStringToBooleanArray(substitution(rotationW1));

        boolean[] w0Rcon = xorArrays(leftPartKeyConverted, Rcon);
        boolean[] W2 = xorArrays(w0Rcon, subRotationW1);

        boolean[] W3 = xorArrays(W2, rightPartKeyConverted);

        String W3String = booleanArrayToString(W3);
        String W3SubstitutionSwapped = swapHalves(W3String);
        String substitutionOnW3 = substitution(W3SubstitutionSwapped);

        boolean[] xorW4 = xorArrays(W2, Rcon2);
        boolean[] substitutionW3Result = convertStringToBooleanArray(substitutionOnW3);
        boolean[] W4 = xorArrays(xorW4, substitutionW3Result);

        boolean[] W5 = xorArrays(W4, W3);

        boolean[] K0 = concatenateBooleanArrays(leftPartKeyConverted, rightPartKeyConverted);
        boolean[] K1 = concatenateBooleanArrays(W2, W3);
        boolean[] K2 = concatenateBooleanArrays(W4, W5);

        boolean[] plainTextBool = convertStringToBooleanArray(PlainText);
        boolean[] xorPlainTextK0 = xorArrays(plainTextBool, K0);

        String xorPtK0 = booleanArrayToString(xorPlainTextK0);

        // Debug statement
        System.out.println("xorPtK0 length: " + xorPtK0.length());

        // Fixing the swapNibbles function call
        if (xorPtK0.length() != 16) {
            throw new IllegalArgumentException("XOR result length is not 16 bits.");
        }

        String swapped2ndNibble4thNibble = swapNibbles(xorPtK0);
        System.out.println("Swapped 2nd and 4th nibble: " + swapped2ndNibble4thNibble);

        boolean[] mixedColumnsK1 = mixedColumnOperation(convertStringToBooleanArray(swapped2ndNibble4thNibble));

        // Debug statement
        System.out.println("mixedColumnsK1 length: " + mixedColumnsK1.length);

        String xorMixedColumnsK1String = booleanArrayToString(mixedColumnsK1);

        // Debug statement
        System.out.println("xorMixedColumnsK1String length: " + xorMixedColumnsK1String.length());

        if (xorMixedColumnsK1String.length() != 16) {
            throw new IllegalArgumentException("XOR mixed columns result length is not 16 bits.");
        }

        String swapped2ndNibble4thNibbleFinal = swapNibbles(xorMixedColumnsK1String);
        boolean[] finalMixedColumnsResult = mixedColumnOperation(convertStringToBooleanArray(swapped2ndNibble4thNibbleFinal));

        boolean[] cipherText = xorArrays(finalMixedColumnsResult, K2);

        String cipherTextString = booleanArrayToString(cipherText);
        String cipherTextHex = binaryStringToHexString(cipherTextString);

        System.out.println("Cipher Text: " + cipherTextHex);
    }
}

public class Main {
    public static void main(String[] args) {
        AES aes = new AES();
        aes.encrypt();
    }
}
