package fr.aboucorp.variantchess.test;

import org.junit.Assert;
import org.junit.Test;

import fr.aboucorp.variantchess.app.multiplayer.Metadata;

public class MetadataTest {

    @Test
    public void getMetadataFromString() {
        String okString = "{\"email\":\"test@test.com\",\"searchGoogleAccount\":\"true\"}";
        Metadata metadata = new Metadata();
        metadata.values.put("email","test@test.com");
        metadata.values.put("searchGoogleAccount",Boolean.toString(true));
        Assert.assertEquals(Metadata.getJsonFromMetadata(metadata),okString);
    }

    @Test
    public void getJsonFromMetadata() {
        String okString = "{\"email\":\"test@test.com\",\"searchGoogleAccount\":\"true\"}";
        Metadata metadata = Metadata.getMetadataFromString(okString);
        Assert.assertEquals(metadata.values.get("email"),"test@test.com");
        Assert.assertEquals(metadata.values.get("searchGoogleAccount"),"true");
    }
}