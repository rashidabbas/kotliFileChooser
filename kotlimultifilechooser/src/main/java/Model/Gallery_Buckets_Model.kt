package Models

public class Gallery_Buckets_Model {

    var BucketName: String
    var BucketId: String
    var BucketSize: Int = 0
    var ImagePath: String
    var isSelected: Boolean = false
    var stringType = ""

    constructor(bucketName: String, bucketSize: Int, imagePath: String, bucketId: String,stringType : String = "") {
        this.BucketName = bucketName
        this.BucketSize = bucketSize
        this.ImagePath = imagePath
        this.BucketId = bucketId
        this.isSelected = false
        this.stringType = stringType
    }
}
