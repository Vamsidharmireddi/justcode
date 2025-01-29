import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            // Load and parse JSON files
            JSONObject testCase1 = readJsonFromFile("testcase1.json");
            JSONObject testCase2 = readJsonFromFile("testcase2.json");

            // Solve for the secret (constant term c) in both test cases
            System.out.println("Secret for Test Case 1: " + solveSecret(testCase1));
            System.out.println("Secret for Test Case 2: " + solveSecret(testCase2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject readJsonFromFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return new JSONObject(content.toString());
    }

    private static BigInteger solveSecret(JSONObject testCase) {
        JSONObject keys = testCase.getJSONObject("keys");
        int k = keys.getInt("k");
        int m = k - 1;

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        for (String key : testCase.keySet()) {
            if (!key.equals("keys")) {
                JSONObject root = testCase.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = root.getInt("base");
                BigInteger y = new BigInteger(root.getString("value"), base);

                xValues.add(BigInteger.valueOf(x));
                yValues.add(y);
            }
        }

        // Apply Lagrange Interpolation to find the constant term (c)
        return lagrangeInterpolation(xValues, yValues, m);
    }

    private static BigInteger lagrangeInterpolation(List<BigInteger> xValues, List<BigInteger> yValues, int degree) {
        BigInteger result = BigInteger.ZERO;
        int k = xValues.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = yValues.get(i);
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term = term.multiply(xValues.get(j).negate());
                    denominator = denominator.multiply(xValues.get(i).subtract(xValues.get(j)));
                }
            }
            result = result.add(term.divide(denominator));
        }
        return result;
    }
}
