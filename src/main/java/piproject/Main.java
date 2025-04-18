package piproject;

import piproject.models.Product;
import piproject.models.Category;  // Import the Category model
import piproject.services.ProductService;
import piproject.services.CategoryService;  // Import the CategoryService

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductService service = new ProductService();
        CategoryService categoryService = new CategoryService();  // Create CategoryService instance

        while (true) {
            System.out.println("\n--- PRODUCT MANAGEMENT MENU ---");
            System.out.println("1. Add product");
            System.out.println("2. Delete product");
            System.out.println("3. Update product");
            System.out.println("4. Find product by name or id");
            System.out.println("5. List all products");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    Product p = inputProduct(scanner, false, categoryService);  // Pass CategoryService
                    service.add(p);
                    break;

                case 2:
                    System.out.print("Enter product ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    Product toDelete = new Product();
                    toDelete.setId(deleteId);
                    service.delete(toDelete);
                    break;

                case 3:
                    System.out.print("Enter product ID to update: ");
                    int updateId = Integer.parseInt(scanner.nextLine());
                    Product updatedProduct = inputProduct(scanner, true, categoryService);  // Pass CategoryService
                    updatedProduct.setId(updateId);
                    service.update(updatedProduct);
                    break;

                case 4:
                    System.out.print("Enter product name or ID: ");
                    String search = scanner.nextLine();
                    Product found = service.find(search);
                    if (found != null) {
                        System.out.println("✅ Found: " + found);
                    } else {
                        System.out.println("❌ Product not found.");
                    }
                    break;

                case 5:
                    List<Product> list = service.getAll();
                    if (list.isEmpty()) {
                        System.out.println("📦 No products found.");
                    } else {
                        list.forEach(System.out::println);
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

    private static Product inputProduct(Scanner scanner, boolean showId, CategoryService categoryService) {
        if (showId) System.out.println("Enter new product details:");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Coupon: ");
        int coupon = Integer.parseInt(scanner.nextLine());

        System.out.print("Value: ");
        float value = Float.parseFloat(scanner.nextLine());

        System.out.print("State (etat): ");
        String etat = scanner.nextLine();

        System.out.print("Availability (dispo): ");
        String dispo = scanner.nextLine();

        System.out.print("Category: ");
        String categoryName = scanner.nextLine();

        // Fetch the Category object from the CategoryService
        List<Category> categories = categoryService.getAll();  // Get all categories from the CategoryService

        // Find the category with the matching name
        Category category = null;
        for (Category c : categories) {
            if (c.getCategory_name().equalsIgnoreCase(categoryName)) {
                category = c;
                break;
            }
        }

        // If the category doesn't exist, handle the error
        if (category == null) {
            System.out.println("❌ Category not found.");
            return null;
        }

        System.out.print("Image URL: ");
        String image = scanner.nextLine();

        return new Product(name, desc, coupon, value, etat, dispo, category, image);  // Pass the Category object
    }
}
