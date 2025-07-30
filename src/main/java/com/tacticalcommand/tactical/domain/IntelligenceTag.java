package com.tacticalcommand.tactical.domain;

import jakarta.persistence.*;

/**
 * Entity representing tags for intelligence reports to enable categorization and search.
 */
@Entity
@Table(name = "intelligence_tags")
public class IntelligenceTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @Column(name = "tag_category")
    private String tagCategory; // LOCATION, THREAT, ASSET, OPERATION, etc.

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intelligence_report_id")
    private IntelligenceReport intelligenceReport;

    // Default constructor
    public IntelligenceTag() {}

    // Constructor
    public IntelligenceTag(String tagName, String tagCategory) {
        this.tagName = tagName;
        this.tagCategory = tagCategory;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagCategory() {
        return tagCategory;
    }

    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IntelligenceReport getIntelligenceReport() {
        return intelligenceReport;
    }

    public void setIntelligenceReport(IntelligenceReport intelligenceReport) {
        this.intelligenceReport = intelligenceReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntelligenceTag)) return false;
        IntelligenceTag that = (IntelligenceTag) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntelligenceTag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                ", tagCategory='" + tagCategory + '\'' +
                '}';
    }
}
