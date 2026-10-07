
package src;

public class Book {

    int bookId;
    String title;
    String author;
    int available;

    public Book(int bookId, String title, String author, int available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public void displayBook() {
        System.out.println(
            bookId + " | " + title + " | " + author + " | " + available
        );
    }
}