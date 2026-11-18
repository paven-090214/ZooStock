package ZooStock.zoostock_backend.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AnimalApiResponse {

    List<AnimalApiData> results;

    public List<AnimalApiData> getResults() {
        return results;
    }

    public void setResults(List<AnimalApiData> results) {
        this.results = results;
    }

    public static class AnimalApiData {
        Long id;
        String name;
        String rank;
        @JsonProperty("preferred_common_name")
        String preferredCommonName;

        public AnimalApiData(
                Long id,
                String name,
                String rank,
                String preferredCommonName
        ) {
            this.id = id;
            this.name = name;
            this.rank = rank;
            this.preferredCommonName = preferredCommonName;
        }

        @JsonProperty("default_photo")
        private PhotoData defaultPhoto;

        public PhotoData getDefaultPhoto() {
            return defaultPhoto;
        }

        public void setDefaultPhoto(PhotoData defaultPhoto) {
            this.defaultPhoto = defaultPhoto;
        }

        public String getPreferredCommonName() {
            return preferredCommonName;
        }

        public void setPreferredCommonName(String preferredCommonName) {
            this.preferredCommonName = preferredCommonName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
    }

    public static class PhotoData {
        Long id;
        @JsonProperty("license_code")
        String licenseCode;
        @JsonProperty("attribution_name")
        String attributionName;
        @JsonProperty("medium_url")
        String mediumUrl;

        public PhotoData() {
        }

        public PhotoData(Long id, String licenseCode, String attributionName, String mediumUrl) {
            this.id = id;
            this.licenseCode = licenseCode;
            this.attributionName = attributionName;
            this.mediumUrl = mediumUrl;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLicenseCode() {
            return licenseCode;
        }

        public void setLicenseCode(String licenseCode) {
            this.licenseCode = licenseCode;
        }

        public String getAttributionName() {
            return attributionName;
        }

        public void setAttributionName(String attributionName) {
            this.attributionName = attributionName;
        }

        public String getMediumUrl() {
            return mediumUrl;
        }

        public void setMediumUrl(String mediumUrl) {
            this.mediumUrl = mediumUrl;
        }
    }
}
