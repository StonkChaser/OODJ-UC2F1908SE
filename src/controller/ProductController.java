package controller;

import javafx.animation.TranslateTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.DuplicateException;
import model.IOWriterReader;
import model.NullValueException;
import model.UtilisedException;
import model.objects.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ProductController implements Initializable {
    private static Product selectedProductToView = null;

    @FXML AnchorPane categoriesPane;
    @FXML AnchorPane addProductPane;
    @FXML AnchorPane editProductPane;

    @FXML ComboBox searchComboBox;
    @FXML TextField searchText;
    @FXML TableView<Product> productTableView;
    @FXML TableColumn productIdTableColumn;
    @FXML TableColumn nameTableColumn;
    @FXML TableColumn categoryIdTableColumn;
    @FXML TableColumn supplierIdTableColumn;
    @FXML TableColumn quantityTableColumn;
    @FXML TableColumn purchasingPriceTableColumn;
    @FXML TableColumn sellingPriceTableColumn;
    @FXML TableColumn profitMarginTableColumn;

    @FXML TableView<Category> categoriesCategoryTableView;
    @FXML TableColumn categoriesCategoryIdTableColumn;
    @FXML TableColumn categoriesCategoryNameTableColumn;
    @FXML TextField categoriesCategoryName;
    @FXML Button categoriesCategoryAddButton;
    @FXML Button categoriesCategoryDeleteButton;

    @FXML TextField addProductName;
    @FXML ComboBox<Category> addProductCategoryId;
    @FXML ComboBox<Supplier> addProductSupplierId;
    @FXML TextField addProductQuantity;
    @FXML TextField addProductPurchasingPrice;
    @FXML TextField addProductSellingPrice;
    @FXML ImageView addUserImageView;

    @FXML TextField editProductName;
    @FXML ComboBox<Category> editProductCategoryId;
    @FXML ComboBox<Supplier> editProductSupplierId;
    @FXML TextField editProductQuantity;
    @FXML TextField editProductPurchasingPrice;
    @FXML TextField editProductSellingPrice;
    @FXML ImageView editUserImageView;

    @FXML Button addProductClearButton;
    @FXML Button editProductClearButton;
    @FXML Button viewLogButton;

    // Setting up animation destination, duration, and object
    static final double actionBarWidth = 100.0;
    static final double outOfBoundsAnchorPane = -350.0 - actionBarWidth;
    static final double startOfBoundsAnchorPane = 0.0;
    static final Duration animationDuration = Duration.seconds(0.1);
    TranslateTransition categoriesPaneOpenAnimation;
    TranslateTransition categoriesPaneCloseAnimation;
    TranslateTransition addProductPaneOpenAnimation;
    TranslateTransition addProductPaneCloseAnimation;
    TranslateTransition editProductPaneOpenAnimation;
    TranslateTransition editProductPaneCloseAnimation;


    public void initialize(URL url, ResourceBundle rb) {
        // Initializing the animation objects with their corresponding pane
        categoriesPaneOpenAnimation = new TranslateTransition(animationDuration, categoriesPane);
        categoriesPaneCloseAnimation = new TranslateTransition(animationDuration, categoriesPane);
        addProductPaneOpenAnimation = new TranslateTransition(animationDuration, addProductPane);
        addProductPaneCloseAnimation = new TranslateTransition(animationDuration, addProductPane);
        editProductPaneOpenAnimation = new TranslateTransition(animationDuration, editProductPane);
        editProductPaneCloseAnimation = new TranslateTransition(animationDuration, editProductPane);

        // Hooking up the animation objects with the destinations
        categoriesPaneOpenAnimation.setToX(outOfBoundsAnchorPane);
        categoriesPaneCloseAnimation.setToX(startOfBoundsAnchorPane);
        addProductPaneOpenAnimation.setToX(outOfBoundsAnchorPane);
        addProductPaneCloseAnimation.setToX(startOfBoundsAnchorPane);
        editProductPaneOpenAnimation.setToX(outOfBoundsAnchorPane);
        editProductPaneCloseAnimation.setToX(startOfBoundsAnchorPane);

        // Populate combo box and set default value
        searchComboBox.getItems().addAll("PID", "Name", "Category", "Supplier",
                                        "Quantity", "Purchasing Price", "Selling Price", "Profit Margin");
        searchComboBox.setValue("Product Name");

        // Configure product table columns and items
        productIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        supplierIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        purchasingPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("purchasingPrice"));
        sellingPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        profitMarginTableColumn.setCellValueFactory(new PropertyValueFactory<>("profitMargin"));
        productTableView.setItems(Product.products);
        productTableView.setRowFactory( tableView -> {
            TableRow<Product> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!tableRow.isEmpty()) ) {
                    Product selectedProduct = tableRow.getItem();
                    selectedProductToView = selectedProduct;
                    Stage viewStage = new Stage();
                    try {
                        viewStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/View.fxml"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    viewStage.setWidth(400);
                    viewStage.setHeight(550);
                    viewStage.setTitle("Product View");
                    viewStage.setResizable(false);
                    viewStage.show();
                }
            });
            return tableRow;
        });

        // Configure category table columns and items
        categoriesCategoryIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        categoriesCategoryNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoriesCategoryTableView.setItems(Category.categories);

        // Initialize categories in add and edit product combobox
        // Set the items inside combobox to show just category name instead of object's toString()
        addProductCategoryId.setItems(Category.categories);
        addProductCategoryId.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                try {
                    return category.getCategoryName();
                } catch(Exception exception){
                    return "";
                }
            }

            @Override
            public Category fromString(String categoryName) {
                return addProductCategoryId.getItems().stream().filter(category ->
                        category.getCategoryName().equals(categoryName)).findFirst().orElse(null);
            }
        });
        editProductCategoryId.setItems(Category.categories);
        editProductCategoryId.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                try {
                    return category.getCategoryName();
                } catch(Exception exception){
                    return "";
                }
            }

            @Override
            public Category fromString(String categoryName) {
                return editProductCategoryId.getItems().stream().filter(category ->
                        category.getCategoryName().equals(categoryName)).findFirst().orElse(null);
            }
        });

        // Initialize suppliers in add and edit product combobox
        addProductSupplierId.setItems(Supplier.suppliers);
        addProductSupplierId.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                try {
                    return supplier.getName();
                } catch(Exception exception){
                    return "";
                }
            }

            @Override
            public Supplier fromString(String supplierName) {
                return addProductSupplierId.getItems().stream().filter(supplier ->
                        supplier.getName().equals(supplierName)).findFirst().orElse(null);
            }
        });
        editProductSupplierId.setItems(Supplier.suppliers);
        editProductSupplierId.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                try {
                    return supplier.getName();
                } catch(Exception exception){
                    return "";
                }
            }

            @Override
            public Supplier fromString(String supplierName) {
                return editProductSupplierId.getItems().stream().filter(supplier ->
                        supplier.getName().equals(supplierName)).findFirst().orElse(null);
            }
        });

        // Disable view log button
        if (LoginController.getInstance().getClass() == Product_Manager.class) {
            viewLogButton.setDisable(true);
        }
    }

    public void categoriesButton_OnAction(Event event) {
        if (categoriesPane.getTranslateX() != outOfBoundsAnchorPane) {
            resetPanes();
            categoriesPaneOpenAnimation.play();
        }
        else {
            categoriesPaneCloseAnimation.play();
        }
    }

    public void categoriesCategoryAddButton_OnAction(Event event) throws IOException {
        Category.categories.add(new Category(categoriesCategoryName.getText()));
        Log.productLogs.add(new Log("Added category: " + categoriesCategoryName.getText()));
        refreshCategoryTableView();
    }

    public void categoriesCategoryDeleteButton_OnAction(Event event) throws IOException {
        try {
            Category selectedCategory = categoriesCategoryTableView.getSelectionModel().getSelectedItem();
            for (Product product: Product.products) {
                if (product.getCategoryId() == selectedCategory.getCategoryId()) {
                    throw new UtilisedException();
                }
            }
            Log.productLogs.add(new Log("Deleted category: " + selectedCategory.getCategoryName()));
            Category.categories.remove(selectedCategory);
            refreshCategoryTableView();
        } catch (UtilisedException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Category is being used by a product.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        }
    }

    public void categoriesCategoryTableView_OnSelect(Event event) {
        Category selectedCategory = categoriesCategoryTableView.getSelectionModel().getSelectedItem();
        categoriesCategoryName.setText(selectedCategory.getCategoryName());
    }

    public void addProductButton_OnAction(Event event) {
        if (addProductPane.getTranslateX() != outOfBoundsAnchorPane) {
            resetPanes();
            addProductPaneOpenAnimation.play();
        }
        else {
            addProductPaneCloseAnimation.play();
        }
        if (addUserImageView.getImage() == null) {
            Image placeholderImage = new Image("/images/Product_Image_Placeholder.png");
            addUserImageView.setImage(placeholderImage);
        }
    }

    public void addUserUploadImageButton_OnAction (Event event) {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        File imageFile = fileChooser.showOpenDialog(mainStage);
        Image image = new Image(imageFile.toURI().toString());
        addUserImageView.setImage(image);
    }

    public void addUserClearImageButton_OnAction (Event event) {
        addUserImageView.setImage(null);
    }

    public void addProductSubmitButton_OnAction (Event event) throws IOException {
        try {
            for (Product product: Product.products) {
                if (product.getName().equals(addProductName.getText())) {
                    throw new DuplicateException();
                }
            }
            if (addProductName.getText() == null || addProductCategoryId.getValue() == null || addProductSupplierId.getValue() == null ||
                    addProductQuantity.getText() == null || addProductPurchasingPrice.getText() == null || addProductSellingPrice.getText() == null) {
                throw new NullValueException();
            }
            Product newProduct = new Product(addProductName.getText(), Integer.parseInt(addProductCategoryId.getValue().toString().split("\\|")[0]),
                    Integer.parseInt(addProductSupplierId.getValue().toString().split("\\|")[0]), Integer.parseInt(addProductQuantity.getText()),
                    Double.parseDouble(addProductPurchasingPrice.getText()), Double.parseDouble(addProductSellingPrice.getText()));
            Product.products.add(newProduct);
            Log.productLogs.add(new Log("Added product: " + newProduct.getName()));

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(addUserImageView.getImage(), null);
            File outputFile = new File(IOWriterReader.productImagesDirectory + newProduct.getProductId());
            ImageIO.write(bufferedImage, "jpg", outputFile);

            addProductClearButton.fire();
            addProductPaneCloseAnimation.play();
            refreshTableView();
        } catch (DuplicateException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Product name has already been used.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (NumberFormatException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Quantity and prices must be a number.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (NullValueException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("All fields except image must be filled.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        }
    }

    public void addProductClearButton_OnAction (Event event) {
        addProductName.setText(null);
        addProductCategoryId.setValue(null);
        addProductSupplierId.setValue(null);
        addProductQuantity.setText(null);
        addProductPurchasingPrice.setText(null);
        addProductSellingPrice.setText(null);
        addUserImageView.setImage(null);
    }

    public void addProductCancelButton_OnAction (Event event) {
        addProductClearButton.fire();
        addProductPaneCloseAnimation.play();
    }

    public void editProductButton_OnAction(Event event) {
        try {
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                throw new NullPointerException();
            }

            if (editProductPane.getTranslateX() != outOfBoundsAnchorPane) {
                resetPanes();
                editProductPaneOpenAnimation.play();
            }
            else {
                editProductPaneCloseAnimation.play();
            }

            editProductName.setText(selectedProduct.getName());
            Predicate<Category> categoryPredicate = category -> category.getCategoryId() == selectedProduct.getCategoryId();
            editProductCategoryId.setValue(Category.categories.filtered(categoryPredicate).get(0));
            Predicate<Supplier> supplierPredicate = supplier -> supplier.getSupplierId() == selectedProduct.getSupplierId();
            editProductSupplierId.setValue(Supplier.suppliers.filtered(supplierPredicate).get(0));
            editProductQuantity.setText(String.valueOf(selectedProduct.getQuantity()));
            editProductPurchasingPrice.setText(String.valueOf(selectedProduct.getPurchasingPrice()));
            editProductSellingPrice.setText(String.valueOf(selectedProduct.getSellingPrice()));
            try {
                Image loadedImage = new Image("/data/product images/" + selectedProduct.getProductId());
                editUserImageView.setImage(loadedImage);
            } catch (IllegalArgumentException exception) {
                Image placeholderImage = new Image("/images/Product_Image_Placeholder.png");
                editUserImageView.setImage(placeholderImage);
            }

            editProductPaneCloseAnimation.play();
        } catch (NullPointerException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Product must be selected.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        }
    }

    public void editUserUploadImageButton_OnAction (Event event) {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        File imageFile = fileChooser.showOpenDialog(mainStage);
        Image image = new Image(imageFile.toURI().toString());
        editUserImageView.setImage(image);
    }

    public void editUserClearImageButton_OnAction (Event event) {
        editUserImageView.setImage(null);
    }

    public void editProductSubmitButton_OnAction (Event event) {
        try {
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            for (Product product: Product.products) {
                if (product.getName().equals(editProductName.getText()) &&
                        !editProductName.getText().equals(selectedProduct.getName())) {
                    throw new DuplicateException();
                }
            }
            if (editProductName.getText() == null || editProductCategoryId.getValue() == null || editProductSupplierId.getValue() == null ||
                    editProductQuantity.getText() == null || editProductPurchasingPrice.getText() == null || editProductSellingPrice.getText() == null) {
                throw new NullValueException();
            }
            selectedProduct.setName(editProductName.getText());
            selectedProduct.setCategoryId(Integer.parseInt(editProductCategoryId.getValue().toString().split("\\|")[0]));
            selectedProduct.setSupplierId(Integer.parseInt(editProductSupplierId.getValue().toString().split("\\|")[0]));
            selectedProduct.setQuantity(Integer.parseInt(editProductQuantity.getText()));
            selectedProduct.setPurchasingPrice(Double.parseDouble(editProductPurchasingPrice.getText()));
            selectedProduct.setSellingPrice(Double.parseDouble(editProductSellingPrice.getText()));
            Log.productLogs.add(new Log("Edited product: " + selectedProduct.getName()));

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(editUserImageView.getImage(), null);
            File outputFile = new File(IOWriterReader.productImagesDirectory + selectedProduct.getProductId());
            ImageIO.write(bufferedImage, "jpg", outputFile);

            editProductClearButton.fire();
            editProductPaneCloseAnimation.play();
            refreshTableView();
        } catch (DuplicateException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Product name has already been used.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (NumberFormatException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Quantity and prices must be a number.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (NullValueException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("All fields must be filled.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editProductClearButton_OnAction (Event event) {
        editProductName.setText(null);
        editProductCategoryId.setValue(null);
        editProductSupplierId.setValue(null);
        editProductQuantity.setText(null);
        editProductPurchasingPrice.setText(null);
        editProductSellingPrice.setText(null);
        editUserImageView.setImage(null);
    }

    public void editProductCancelButton_OnAction (Event event) {
        editProductPaneCloseAnimation.play();
    }

    public void deleteProductButton_OnAction(Event event) {
        try {
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                throw new NullPointerException();
            }

            Alert confirmationPopup = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete the product permanently?",
                    ButtonType.YES, ButtonType.NO);
            confirmationPopup.showAndWait();

            if (confirmationPopup.getResult() == ButtonType.YES) {
                for (Catalogue catalogue: Catalogue.catalogues) {
                    if (catalogue.getProductsId().contains(selectedProduct.getProductId())) {
                        throw new UtilisedException();
                    }
                }
                Product.products.remove(selectedProduct);
                Log.productLogs.add(new Log("Deleted product: " + selectedProduct.getName()));
            }
            refreshTableView();
        } catch (NullPointerException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Product must be selected.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (UtilisedException exception) {
            Dialog dialog = new Dialog();
            dialog.setContentText("Product is being used in a catalogue.");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        }
    }

    public void viewLogButton_OnAction (Event event) throws IOException {
        Stage logStage = new Stage();
        logStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Log.fxml"))));
        logStage.setWidth(800);
        logStage.setHeight(600);
        logStage.setTitle("Product Log");
        logStage.show();
    }

    public void resetPanes() {
        categoriesPaneCloseAnimation.play();
        addProductPaneCloseAnimation.play();
        editProductPaneCloseAnimation.play();
    }

    private void refreshTableView() {
        productTableView.getColumns().get(0).setVisible(false);
        productTableView.getColumns().get(0).setVisible(true);
    }

    private void refreshCategoryTableView() {
        categoriesCategoryTableView.refresh();
    }

    public void searchText_OnChange (Event event) {
        Predicate<Product> searchComboBoxValue = null;
        switch (searchComboBox.getValue().toString()) {
            case "ID":
                searchComboBoxValue = product -> String.valueOf(product.getProductId()).contains(searchText.getText());
                break;
            case "Name":
                searchComboBoxValue = product -> product.getName().contains(searchText.getText());
                break;
            case "Category":
                searchComboBoxValue = product -> String.valueOf(product.getCategoryName()).contains(searchText.getText());
                break;
            case "Supplier":
                searchComboBoxValue = product -> String.valueOf(product.getSupplierName()).contains(searchText.getText());
                break;
            case "Quantity":
                searchComboBoxValue = product -> String.valueOf(product.getQuantity()).contains(searchText.getText());
                break;
            case "Purchasing Price":
                searchComboBoxValue = product -> String.valueOf(product.getPurchasingPrice()).contains(searchText.getText());
                break;
            case "Selling Price":
                searchComboBoxValue = product -> String.valueOf(product.getSellingPrice()).contains(searchText.getText());
                break;
            case "Profit Margin":
                searchComboBoxValue = product -> String.valueOf(product.getProfitMargin()).contains(searchText.getText());
                break;
        }
        productTableView.setItems(Product.products.filtered(searchComboBoxValue));
    }

    public static Product getSelectedProductToView() {
        return selectedProductToView;
    }
}
