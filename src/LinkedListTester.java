/** From ECSE 132 lab
 * Santiago grew up New Jersey
 * Jack grew up in Indiano
 * Ignas did no grew up in a city
 */
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test classes LinkedList and LLNode
 */
public class LinkedListTester {
  

  /**
   * Test the toString method
   */
  @Test
  public void testToString() {
    LinkedList<Integer> list = new LinkedList<Integer>();
    assertEquals("Testing empty list", "", list.toString());
    list.addToFront(3);
    assertEquals("Testing list of one node", "3", list.toString());
    list.addToFront(2);
    assertEquals("Testing list of two nodes", "23", list.toString());
    list.addToFront(1);
    assertEquals("Testing list of three nodes", "123", list.toString());
  }
  
  /**
   * Test the contains method
   */
  @Test
  public void testContains() {
//    fail("A test that always fails");
    LinkedList<Integer> list = new LinkedList<Integer>();
    assertEquals("Testing empty list", false, list.contains(3));
    list.addToFront(9);
    assertEquals("Testing just after adding 9", true, list.contains(9));
    list.addToFront(8);
    assertEquals("Testing just after adding 8", true, list.contains(8));
    list.addToFront(7);
    list.addToFront(6);
    list.addToFront(5);
    list.addToFront(4);
    list.addToFront(3);
    assertEquals("Testing later after adding 9", true, list.contains(9));
    assertEquals("Testing later after adding 7", true, list.contains(7));
      assertFalse("Testing with an element which does not exist", list.contains(999));

  }
  
  /**
   * Test the remove method
   */
  @Test
  public void testRemove() {
    LinkedList<Integer> list = new LinkedList<Integer>();
    assertEquals("Removing element not in list", "", list.toString());
    list.addToFront(4);
    list.addToFront(3);
    list.addToFront(2);
    list.addToFront(1);
    list.remove(5);
    assertEquals("Removing element not in list", "1234", list.toString());
    list.remove(3);
    assertEquals("Removing middle element", "124", list.toString());
    list.remove(1);
    assertEquals("Removing first element", "24", list.toString());
    list.remove(4);
    assertEquals("Removing last element", "2", list.toString());
    list.remove(2);
    assertEquals("Removing only element", "", list.toString());
    list.remove(0);
    assertEquals("Removing from empty list", "", list.toString());
  }
  
}
