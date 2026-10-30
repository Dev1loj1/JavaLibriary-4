import java.util.Objects;

public class Book {
    private final String genre;
    private final String author;
    private final String title;
    private final int year;

    public Book(String genre, String author, String title, int year) {
        this.genre = genre;
        this.author = author;
        this.title = title;
        this.year = year;
    }

    public String getGenre() { return genre; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public int getYear() { return year; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Book book)) return false;
        return Objects.equals(author, book.author) && Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() { return Objects.hash(author, title); }

    @Override
    public String toString() {
        return "Book{genre='" + genre + "', author='" + author + "', title='" + title + "', year=" + year + "}";
    }
}
