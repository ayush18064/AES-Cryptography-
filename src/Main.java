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
        // taking the 16 bit plaintext input
        System.out.println("Enter the 16 bit plaintext: ");
        PlainText = sc.nextLine();
        System.out.println("Enter the 16 bit Key: ");
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

    String substituiton(String str) {

        int row1 = Integer.parseInt("" + str.charAt(0) + str.charAt(1), 2); // 1
        System.out.println(row1);

        int col1 = Integer.parseInt("" + str.charAt(2) + str.charAt(3), 2); // 1
        System.out.println(col1);

        int row2 = Integer.parseInt("" + str.charAt(4) + str.charAt(5), 2);// 3
        System.out.println(row2);

        int col2 = Integer.parseInt("" + str.charAt(6) + str.charAt(7), 2);// 3
        System.out.println(col2);

        int firstHalf = substitution[row1][col1];
        int secondHalf = substitution[row2][col2];
        System.out.println(firstHalf);
        System.out.println(secondHalf);
        String firstHalf1 = integerToBinaryString(firstHalf, 4);
        String secondHalf2 = integerToBinaryString(secondHalf, 4);
        String finalResult = firstHalf1 + secondHalf2;
        System.out.println(finalResult);
        return finalResult;


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
        // Ensure the hexadecimal string has the correct length
        int len = (int) Math.ceil(binaryStr.length() / 4.0);
        return String.format("%" + len + "s", hexStr).replace(' ', '0');
    }

    String swapNibbles(String str) {
        if (str.length() != 16) {
            throw new IllegalArgumentException("String length must be 16 bits.");
        }
        StringBuilder sb = new StringBuilder(str);
        String nibble2 = sb.substring(4, 8); // Second nibble
        String nibble4 = sb.substring(12, 16); // Fourth nibble

        // Swap nibbles
        sb.replace(4, 8, nibble4);
        sb.replace(12, 16, nibble2);

        return sb.toString();
    }

}


public class Main {
    public static void main(String[] args) {
        AES aes = new AES();
        aes.keyGeneration();

        // Use the Split method
        String[] splitResult = aes.Split(aes.Key);

        // Print the results
        System.out.println("W0 part: " + splitResult[0]);
        System.out.println("W1 part: " + splitResult[1]);
        System.out.println("This is the left part of the String");
        boolean[] leftPartKeyConverted = aes.convertStringToBooleanArray(splitResult[0]);
        boolean[] RightPartKeyConverted = aes.convertStringToBooleanArray(splitResult[1]);

        // W2 key generation
        // perform the rotaion operation on W1
        String RotationW1 = aes.swapHalves(splitResult[1]);
        System.out.println("Rotation operation on W1");
        System.out.println(RotationW1);

        // converting the String to integer array to interpret the array in binary form


        boolean[] SubNin_rotation_operation = aes.convertStringToBooleanArray(aes.substituiton(RotationW1));
        System.out.println("Result");
        for (boolean val : SubNin_rotation_operation) {
            System.out.print(val + " ");
        }
        boolean w0_Rcon[] = new boolean[8];
        // performing XOR opearation
        for (int i = 0; i < 8; i++) {
            w0_Rcon[i] = leftPartKeyConverted[i] ^ aes.Rcon[i];
        }
        boolean W2[] = new boolean[8];
        for (int i = 0; i < 8; i++) {
            W2[i] = w0_Rcon[i] ^ SubNin_rotation_operation[i];
        }
        System.out.println("W2");
        for (boolean val : W2) {
            System.out.print(val ? 1 : 0);
        }
        // now finding w3 and w4
        // rotate w2 and perform substitution operation
        //XOR the result with W1 to get W3

        //w3=W2+W1;

        boolean w3[] = new boolean[8];
        for (int i = 0; i < 8; i++) {
            w3[i] = W2[i] ^ RightPartKeyConverted[i];
        }
        System.out.println("W3 is ");
        for (boolean val : w3) {
            System.out.print(val ? 1 : 0);
        }

        boolean w4[] = new boolean[8];

        // calculate w4
        // perform swap operation on w3
        String W3String = aes.booleanArrayToString(w3);

        System.out.println("Swapped W3 digits\n");
        String W3_Substitution_swapped = aes.swapHalves(W3String);
        System.out.println(W3_Substitution_swapped);
        String Substitution_On_W3 = aes.substituiton(W3_Substitution_swapped);
        System.out.println("Substitution on W3\n");
        System.out.println(Substitution_On_W3);
        // perfrom XOR operation
        boolean Xor_w4[] = new boolean[8];
        for (int i = 0; i < 8; i++) {
            Xor_w4[i] = W2[i] ^ aes.Rcon2[i];
        }
        boolean substitution_w3_result[] = aes.convertStringToBooleanArray(Substitution_On_W3);
        for (int i = 0; i < 8; i++) {
            w4[i] = Xor_w4[i] ^ substitution_w3_result[i];
        }
        System.out.println("W4 is: ");
        for (boolean val : w4) {
            System.out.print(val ? 1 : 0);
        }
        boolean w5[] = new boolean[8];
        for (int i = 0; i < 8; i++) {
            w5[i] = w4[i] ^ w3[i];
        }
        System.out.println("w5 value\n");
        for (boolean val : w5) {
            System.out.print(val ? 1 : 0);
        }
        System.out.println("\n");

        // KO
        // K1
        // k2
        boolean K0[] = new boolean[16];
        boolean K1[] = new boolean[16];
        boolean K2[] = new boolean[16];


        K0 = aes.concatenateBooleanArrays(leftPartKeyConverted, RightPartKeyConverted);
        System.out.print("K0:");
        for (boolean val : K0) {
            System.out.print(val ? 1 + " " : 0 + " ");
        }
        System.out.print("\nK1:");
        K1 = aes.concatenateBooleanArrays(W2, w3);
        for (boolean val : K1) {
            System.out.print(val ? 1 + " " : 0 + " ");
        }
        System.out.print("\nK2:");
        K2 = aes.concatenateBooleanArrays(w4, w5);
        for (boolean val : K2) {
            System.out.print(val ? 1 + " " : 0 + " ");
        }
        String K0_res = aes.booleanArrayToString(K0);
        String K1_res = aes.booleanArrayToString(K1);
        String K2_res = aes.booleanArrayToString(K2);
        String Hex_K0 = aes.binaryStringToHexString(K0_res);
        String Hex_K1 = aes.binaryStringToHexString(K1_res);
        String Hex_K2 = aes.binaryStringToHexString(K2_res);
        System.out.println("\n");
        System.out.println("K0:" + Hex_K0);
        System.out.println("K1:" + Hex_K1);
        System.out.println("K2:" + Hex_K2);

        // XOR the plain text with K0
        boolean PlainTextBool[] = new boolean[16];
        PlainTextBool = aes.convertStringToBooleanArray(aes.PlainText);

        boolean XOR_PlainText_K0[] = new boolean[16];
        for (int i = 0; i < 16; i++) {
            XOR_PlainText_K0[i] = PlainTextBool[i] ^ K0[i];
        }
        System.out.println("XOR K0 with PlainText");
        for (boolean val : XOR_PlainText_K0) {
            System.out.print(val ? 1 + " " : 0 + " ");
        }
        // now swapping the 2nd nibble and the 4th nibble
        String XOR_Pt_K0 = aes.booleanArrayToString(XOR_PlainText_K0);
        String Swapped_2nd_nibble_4th_nibble = aes.swapNibbles(XOR_Pt_K0);
        System.out.println("Swapped 2nd and 4th nibble: " + Swapped_2nd_nibble_4th_nibble);
        // perform Mixed Column operation


    }
}
