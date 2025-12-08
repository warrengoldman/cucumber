package stepDefinitions;

public enum APIResources {
    GetProductByKey("/get/product/{key}"),
    PostProduct("/post/product"),
    ProcessOrder("/process-order");
    private String uri;
    APIResources(String value) {
        uri = value;
    }
    public String getUri() {
        return uri;
    }
}
