import java.lang.reflect.Method;

public class Reflection {

    public static void main(String[] args) {
        // Step 2: Some simple reflection code

        // Create an instance of a String
        String myString = "kskksd";

        // Using the appropriate method in the Object class, get the class of the String
        Class<?> stringClass = myString.getClass();

        Method[] methods = stringClass.getDeclaredMethods();

        // Using a loop, print the name of each method in the array
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }
}
