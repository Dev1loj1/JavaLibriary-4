import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void loadsTenBooksInJournalOrder() {
        List<Book> books = Main.loadBooks();
        assertEquals(10, books.size());
        assertEquals("Солярис", books.getFirst().getTitle());
        assertEquals("Загадка Эндхауза", books.getLast().getTitle());
    }

    @Test
    void equalityUsesOnlyAuthorAndTitle() {
        Book first = new Book("Фантастика", "Лем", "Солярис", 1961);
        Book second = new Book("Другое", "Лем", "Солярис", 2000);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new Book("Фантастика", "Другой", "Солярис", 1961));
    }

    @Test
    void removesDuplicatesAndKeepsFirstAppearance() {
        List<Book> unique = Main.uniqueBooks(Main.loadBooks());
        assertEquals(8, unique.size());
        assertEquals("Солярис", unique.getFirst().getTitle());
        assertEquals("Загадка Эндхауза", unique.getLast().getTitle());
    }

    @Test
    void groupsGenresAlphabeticallyAndKeepsBookOrder() {
        Map<String, List<Book>> groups = Main.groupByGenre(Main.uniqueBooks(Main.loadBooks()));
        assertEquals(List.of("Детектив", "Проза", "Фантастика"), new ArrayList<>(groups.keySet()));
        assertEquals(List.of("Убийство в Восточном экспрессе", "Собака Баскервилей", "Загадка Эндхауза"),
                groups.get("Детектив").stream().map(Book::getTitle).toList());
    }

    @Test
    void sortsEqualYearsByAuthorThenTitle() {
        List<Book> sorted = Main.sortChronologically(Main.uniqueBooks(Main.loadBooks()));
        assertEquals(List.of("Анна Каренина", "Собака Баскервилей", "Загадка Эндхауза",
                "Убийство в Восточном экспрессе", "451 градус по Фаренгейту", "Солярис",
                "Мастер и Маргарита", "Пикник на обочине"),
                sorted.stream().map(Book::getTitle).toList());
    }

    @Test
    void findsYearExtremes() {
        List<Book> unique = Main.uniqueBooks(Main.loadBooks());
        assertEquals(1877, Collections.min(unique, Comparator.comparingInt(Book::getYear)).getYear());
        assertEquals(1972, Collections.max(unique, Comparator.comparingInt(Book::getYear)).getYear());
    }

    @Test
    void countsChristieBooks() {
        List<String> authors = Main.uniqueBooks(Main.loadBooks()).stream().map(Book::getAuthor).toList();
        assertEquals(2, Collections.frequency(authors, "Кристи"));
    }

    @Test
    void protectedListRejectsAddition() {
        List<Book> protectedBooks = Collections.unmodifiableList(Main.uniqueBooks(Main.loadBooks()));
        assertThrows(UnsupportedOperationException.class,
                () -> protectedBooks.add(new Book("Проза", "Автор", "Книга", 2000)));
    }

    @Test
    void removesOldBooksAndCountsRemainingAuthors() {
        List<Book> unique = Main.uniqueBooks(Main.loadBooks());
        assertTrue(unique.removeIf(book -> book.getYear() < 1950));
        assertEquals(4, unique.size());
        assertEquals(Map.of("Брэдбери", 1, "Булгаков", 1, "Лем", 1, "Стругацкие", 1),
                Main.countByAuthor(unique));
        assertEquals(List.of("Брэдбери", "Булгаков", "Лем", "Стругацкие"),
                new ArrayList<>(Main.countByAuthor(unique).keySet()));
    }

    @Test
    void outputContainsRequiredFinalLines() {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("2) Уникальных книг: 8; повторов отброшено: 2"));
        assertTrue(output.contains("8) Издано до 1950 года — убрано: 4; осталось: 4"));
        assertTrue(output.contains("9) Осталось по авторам: {Брэдбери=1, Булгаков=1, Лем=1, Стругацкие=1}"));
    }
}
