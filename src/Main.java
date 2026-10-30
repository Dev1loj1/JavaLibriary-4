import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    private static final Comparator<Book> CHRONOLOGY = Comparator.comparingInt(Book::getYear)
            .thenComparing(Book::getAuthor).thenComparing(Book::getTitle);

    public static void main(String[] args) {
        List<Book> journal = loadBooks();
        List<Book> unique = uniqueBooks(journal);
        Map<String, List<Book>> byGenre = groupByGenre(unique);
        List<Book> chronological = sortChronologically(unique);

        System.out.println("Polin Andrey prpp2");
        System.out.println("1) Записей в журнале: " + journal.size());
        System.out.println("2) Уникальных книг: " + unique.size() + "; повторов отброшено: " + (journal.size() - unique.size()));
        System.out.println("3) Жанров: " + byGenre.size());
        for (Map.Entry<String, List<Book>> entry : byGenre.entrySet()) {
            String titles = entry.getValue().stream().map(Book::getTitle).collect(Collectors.joining(", "));
            System.out.println("   " + entry.getKey() + " (" + entry.getValue().size() + "): " + titles);
        }

        System.out.println("4) Хронология:");
        for (Book book : chronological) {
            System.out.println("   " + book.getYear() + "  " + book.getAuthor() + " — " + book.getTitle());
        }

        Book oldest = Collections.min(unique, Comparator.comparingInt(Book::getYear));
        Book newest = Collections.max(unique, Comparator.comparingInt(Book::getYear));
        System.out.println("5) Самая старая: " + oldest.getAuthor() + " — " + oldest.getTitle() + " (" + oldest.getYear() + ")");
        System.out.println("   Самая новая:  " + newest.getAuthor() + " — " + newest.getTitle() + " (" + newest.getYear() + ")");

        List<String> authors = unique.stream().map(Book::getAuthor).collect(Collectors.toList());
        System.out.println("6) Книг Кристи в зале: " + Collections.frequency(authors, "Кристи"));

        List<Book> protectedBooks = Collections.unmodifiableList(unique);
        try {
            protectedBooks.add(new Book("Проза", "Тест", "Тест", 2000));
        } catch (UnsupportedOperationException exception) {
            System.out.println("7) Попытка добавить в защищённый список: " + exception.getClass().getSimpleName());
        }

        int beforeRemoval = unique.size();
        unique.removeIf(book -> book.getYear() < 1950);
        System.out.println("8) Издано до 1950 года — убрано: " + (beforeRemoval - unique.size()) + "; осталось: " + unique.size());
        System.out.println("9) Осталось по авторам: " + countByAuthor(unique));
    }

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Фантастика", "Лем", "Солярис", 1961));
        books.add(new Book("Проза", "Булгаков", "Мастер и Маргарита", 1967));
        books.add(new Book("Фантастика", "Стругацкие", "Пикник на обочине", 1972));
        books.add(new Book("Детектив", "Кристи", "Убийство в Восточном экспрессе", 1934));
        books.add(new Book("Фантастика", "Лем", "Солярис", 1961));
        books.add(new Book("Проза", "Толстой", "Анна Каренина", 1877));
        books.add(new Book("Детектив", "Конан Дойл", "Собака Баскервилей", 1902));
        books.add(new Book("Фантастика", "Брэдбери", "451 градус по Фаренгейту", 1953));
        books.add(new Book("Проза", "Булгаков", "Мастер и Маргарита", 1967));
        books.add(new Book("Детектив", "Кристи", "Загадка Эндхауза", 1934));
        return books;
    }

    public static List<Book> uniqueBooks(List<Book> books) {
        Set<Book> unique = new LinkedHashSet<>(books);
        return new ArrayList<>(unique);
    }

    public static Map<String, List<Book>> groupByGenre(List<Book> books) {
        Map<String, List<Book>> groups = new TreeMap<>();
        for (Book book : books) {
            groups.computeIfAbsent(book.getGenre(), genre -> new ArrayList<>()).add(book);
        }
        return groups;
    }

    public static List<Book> sortChronologically(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort(CHRONOLOGY);
        return sorted;
    }

    public static Map<String, Integer> countByAuthor(List<Book> books) {
        Map<String, Integer> counts = new TreeMap<>();
        for (Book book : books) counts.merge(book.getAuthor(), 1, Integer::sum);
        return counts;
    }
}
