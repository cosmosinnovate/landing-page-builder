package com.example.landingpagebuilder.domain.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "pages")
@CompoundIndex(
    name = "page_tenant_slug_idx",
    def = "{'tenantId': 1, 'slug': 1}",
    unique = true,
)
data class Page(
    @Id
    val id: String? = null,
    @Indexed
    val tenantId: String,
    @Indexed
    val slug: String,
    val title: String,
    val metaDescription: String? = null,
    val metaKeywords: String? = null,
    val status: PageStatus = PageStatus.DRAFT,
    val content: PageContent = PageContent(),
    val seoSettings: SeoSettings = SeoSettings(),
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val publishedAt: LocalDateTime? = null,
)

enum class PageStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED,
}

data class PageContent(
    val sections: List<ContentSection> = emptyList(),
    val designSettings: DesignSettings = DesignSettings(),
)

data class ContentSection(
    val id: String,
    val type: SectionType,
    val blocks: List<ContentBlock> = emptyList(),
    val settings: SectionSettings = SectionSettings(),
)

enum class SectionType {
    HEADER,
    HERO,
    CONTENT,
    FEATURES,
    TESTIMONIALS,
    CTA,
    FOOTER,
}

data class ContentBlock(
    val id: String,
    val type: BlockType,
    val content: Map<String, Any> = emptyMap(),
    val styling: BlockStyling = BlockStyling(),
)

enum class BlockType {
    HEADING,
    PARAGRAPH,
    IMAGE,
    BUTTON,
    LIST,
    SPACER,
    DIVIDER,
}

data class SectionSettings(
    val backgroundColor: String? = null,
    val padding: Spacing = Spacing(),
    val margin: Spacing = Spacing(),
    val fullWidth: Boolean = false,
    val customCss: String? = null,
)

data class BlockStyling(
    val textAlign: TextAlign = TextAlign.LEFT,
    val fontSize: String? = null,
    val fontWeight: String? = null,
    val color: String? = null,
    val backgroundColor: String? = null,
    val padding: Spacing = Spacing(),
    val margin: Spacing = Spacing(),
    val borderRadius: String? = null,
    val customCss: String? = null,
)

data class Spacing(
    val top: String = "0",
    val right: String = "0",
    val bottom: String = "0",
    val left: String = "0",
)

enum class TextAlign {
    LEFT,
    CENTER,
    RIGHT,
    JUSTIFY,
}

data class DesignSettings(
    val theme: String = "default",
    val primaryColor: String = "#007bff",
    val secondaryColor: String = "#6c757d",
    val fontFamily: String = "Arial, sans-serif",
    val containerWidth: String = "1200px",
    val customCss: String? = null,
)

data class SeoSettings(
    val ogTitle: String? = null,
    val ogDescription: String? = null,
    val ogImage: String? = null,
    val twitterCard: String = "summary",
    val canonicalUrl: String? = null,
    val noIndex: Boolean = false,
    val noFollow: Boolean = false,
)
