package piproject;

import piproject.models.Category;
import piproject.services.CategoryService;

import java.util.List;
import java.util.Scanner;

public class MainC {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CategoryService service = new CategoryService();

        while (true) {
            System.out.println("\n--- CATEGORY MANAGEMENT MENU ---");
            System.out.println("1. Add category");
            System.out.println("2. Delete category");
            System.out.println("3. Update category");
            System.out.println("4. Find category by name or id");
            System.out.println("5. List all categories");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    Category newCategory = inputCategory(scanner, false);
                    service.add(newCategory);
                    break;

                case 2:
                    System.out.print("Enter category ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    Category toDelete = new Category();
                    toDelete.setCategory_id(deleteId);
                    service.delete(toDelete);
                    break;

                case 3:
                    System.out.print("Enter category ID to update: ");
                    int updateId = Integer.parseInt(scanner.nextLine());
                    Category updated = inputCategory(scanner, true);
                    updated.setCategory_id(updateId);
                    service.update(updated);
                    break;

                case 4:
                    System.out.print("Enter category name or ID: ");
                    String search = scanner.nextLine();
                    Category found = service.find(search);
                    if (found != null) {
                        System.out.println("✅ Found: " + found);
                    } else {
                        System.out.println("❌ Category not found.");
                    }
                    break;

                case 5:
                    List<Category> categories = service.getAll();
                    if (categories.isEmpty()) {
                        System.out.println("📦 No categories found.");
                    } else {
                        categories.forEach(System.out::println);
                    }
                    break;

                case 0:
                    System.out.println("👋 Exiting...");
                    return;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static Category inputCategory(Scanner scanner, boolean showId) {
        if (showId) System.out.println("Enter new category details:");

        System.out.print("Category Name: ");
        String name = scanner.nextLine();

        System.out.print("Category Description: ");
        String desc = scanner.nextLine();

        return new Category(name, desc);
    }
}
