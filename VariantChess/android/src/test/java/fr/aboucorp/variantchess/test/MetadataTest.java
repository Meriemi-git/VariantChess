package fr.aboucorp.variantchess.test;

import org.junit.Assert;
import org.junit.Test;

import fr.aboucorp.variantchess.app.multiplayer.Metadata;

public class MetadataTest {

    @Test
    public void getMetadataFromString() {
        String okString = "{\"email\":\"test@test.com\",\"searchGoogleAccount\":\"true\"}";
        Metadata metadata = new Metadata();
        metadata.put("email", "test@test.com");
        metadata.put("searchGoogleAccount", Boolean.toString(true));
        Assert.assertEquals(metadata.getJsonFromMetadata(), okString);
    }

    @Test
    public void getJsonFromMetadata() {
        String okString = "{\"email\":\"test@test.com\",\"searchGoogleAccount\":\"true\"}";
        Metadata metadata = new Metadata();
        metadata.setMetadataFromString(okString);
        Assert.assertEquals(metadata.get("email"), "test@test.com");
        Assert.assertEquals(metadata.get("searchGoogleAccount"), "true");
    }
}