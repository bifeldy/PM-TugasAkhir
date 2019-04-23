package id.ac.umn.pm_tugasakhir;

public class Product {
    private String productName;
    private int productImage;
    private String productDescription;
    private String productCategory;
    private int productStock;
    private int productPrice;

    public Product(String productName, int productImage, String productDescription, String productCategory, int productStock, int productPrice) {
        this.productName = productName;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productStock = productStock;
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductImage() {
        return productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public int getProductStock() {
        return productStock;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }
}
