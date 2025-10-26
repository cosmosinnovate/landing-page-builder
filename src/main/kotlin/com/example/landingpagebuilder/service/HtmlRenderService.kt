package com.example.landingpagebuilder.service

import com.example.landingpagebuilder.domain.model.BlockStyling
import com.example.landingpagebuilder.domain.model.BlockType
import com.example.landingpagebuilder.domain.model.ContentBlock
import com.example.landingpagebuilder.domain.model.ContentSection
import com.example.landingpagebuilder.domain.model.DesignSettings
import com.example.landingpagebuilder.domain.model.Page
import com.example.landingpagebuilder.domain.model.SectionSettings
import com.example.landingpagebuilder.domain.model.SectionType
import com.example.landingpagebuilder.domain.model.Spacing
import com.example.landingpagebuilder.domain.model.Tenant
import org.springframework.stereotype.Service

@Service
class HtmlRenderService {
    /**
     * Render a complete HTML page from page content
     */
    fun renderPage(
        page: Page,
        tenant: Tenant,
    ): String {
        val designSettings = page.content.designSettings
        val baseUrl = getBaseUrl(tenant)

        return buildString {
            appendLine("<!DOCTYPE html>")
            appendLine("<html lang=\"en\">")
            appendLine("<head>")
            appendLine("    <meta charset=\"UTF-8\">")
            appendLine("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            appendLine("    <title>${escapeHtml(page.title)}</title>")

            // Meta description
            page.metaDescription?.let {
                appendLine("    <meta name=\"description\" content=\"${escapeHtml(it)}\">")
            }

            // Meta keywords
            page.metaKeywords?.let {
                appendLine("    <meta name=\"keywords\" content=\"${escapeHtml(it)}\">")
            }

            // SEO meta tags
            renderSeoMetaTags(page, baseUrl)

            // Generated CSS styles
            appendLine("    <style>")
            appendLine(generateCss(designSettings))
            appendLine("    </style>")

            appendLine("</head>")
            appendLine("<body>")

            // Render page content
            page.content.sections.forEach { section ->
                append(renderSection(section, designSettings))
            }

            appendLine("</body>")
            appendLine("</html>")
        }
    }

    /**
     * Render SEO meta tags
     */
    private fun StringBuilder.renderSeoMetaTags(
        page: Page,
        baseUrl: String,
    ) {
        val seo = page.seoSettings

        // Open Graph tags
        seo.ogTitle?.let {
            appendLine("    <meta property=\"og:title\" content=\"${escapeHtml(it)}\">")
        } ?: run {
            appendLine("    <meta property=\"og:title\" content=\"${escapeHtml(page.title)}\">")
        }

        seo.ogDescription?.let {
            appendLine("    <meta property=\"og:description\" content=\"${escapeHtml(it)}\">")
        } ?: page.metaDescription?.let {
            appendLine("    <meta property=\"og:description\" content=\"${escapeHtml(it)}\">")
        }

        seo.ogImage?.let {
            appendLine("    <meta property=\"og:image\" content=\"${escapeHtml(it)}\">")
        }

        appendLine("    <meta property=\"og:url\" content=\"$baseUrl/${page.slug}\">")
        appendLine("    <meta property=\"og:type\" content=\"website\">")

        // Twitter Card tags
        appendLine("    <meta name=\"twitter:card\" content=\"${seo.twitterCard}\">")
        seo.ogTitle?.let {
            appendLine("    <meta name=\"twitter:title\" content=\"${escapeHtml(it)}\">")
        }
        seo.ogDescription?.let {
            appendLine("    <meta name=\"twitter:description\" content=\"${escapeHtml(it)}\">")
        }
        seo.ogImage?.let {
            appendLine("    <meta name=\"twitter:image\" content=\"${escapeHtml(it)}\">")
        }

        // Canonical URL
        seo.canonicalUrl?.let {
            appendLine("    <link rel=\"canonical\" href=\"${escapeHtml(it)}\">")
        } ?: run {
            appendLine("    <link rel=\"canonical\" href=\"$baseUrl/${page.slug}\">")
        }

        // Robots meta tag
        if (seo.noIndex || seo.noFollow) {
            val robotsContent =
                buildList {
                    if (seo.noIndex) add("noindex")
                    if (seo.noFollow) add("nofollow")
                }.joinToString(", ")
            appendLine("    <meta name=\"robots\" content=\"$robotsContent\">")
        }
    }

    /**
     * Generate CSS from design settings and section/block styles
     */
    private fun generateCss(designSettings: DesignSettings): String =
        buildString {
            // Base styles
            appendLine("        * { box-sizing: border-box; }")
            appendLine("        body {")
            appendLine("            margin: 0;")
            appendLine("            padding: 0;")
            appendLine("            font-family: ${designSettings.fontFamily};")
            appendLine("            line-height: 1.6;")
            appendLine("            color: #333;")
            appendLine("        }")

            // Container styles
            appendLine("        .container {")
            appendLine("            max-width: ${designSettings.containerWidth};")
            appendLine("            margin: 0 auto;")
            appendLine("            padding: 0 20px;")
            appendLine("        }")

            // Section styles
            appendLine("        .section {")
            appendLine("            width: 100%;")
            appendLine("        }")

            appendLine("        .section-content {")
            appendLine("            padding: 40px 0;")
            appendLine("        }")

            // Block styles
            appendLine("        .block {")
            appendLine("            margin-bottom: 20px;")
            appendLine("        }")

            appendLine("        .block:last-child {")
            appendLine("            margin-bottom: 0;")
            appendLine("        }")

            // Responsive styles
            appendLine("        @media (max-width: 768px) {")
            appendLine("            .container {")
            appendLine("                padding: 0 15px;")
            appendLine("            }")
            appendLine("            .section-content {")
            appendLine("                padding: 20px 0;")
            appendLine("            }")
            appendLine("        }")

            // Custom CSS
            designSettings.customCss?.let {
                appendLine("        /* Custom CSS */")
                appendLine("        $it")
            }
        }

    /**
     * Render a content section
     */
    private fun renderSection(
        section: ContentSection,
        designSettings: DesignSettings,
    ): String =
        buildString {
            val sectionClass = getSectionClass(section.type)
            val sectionStyle = generateSectionStyle(section.settings)

            appendLine("    <section class=\"section $sectionClass\"$sectionStyle>")

            if (section.settings.fullWidth) {
                section.blocks.forEach { block ->
                    append("        ")
                    appendLine(renderBlock(block))
                }
            } else {
                appendLine("        <div class=\"container\">")
                appendLine("            <div class=\"section-content\">")
                section.blocks.forEach { block ->
                    append("                ")
                    appendLine(renderBlock(block))
                }
                appendLine("            </div>")
                appendLine("        </div>")
            }

            appendLine("    </section>")
        }

    /**
     * Render a content block
     */
    private fun renderBlock(block: ContentBlock): String {
        val blockStyle = generateBlockStyle(block.styling)
        val baseClass = "block ${block.type.name.lowercase()}-block"

        return when (block.type) {
            BlockType.HEADING -> {
                val text = block.content["text"] as? String ?: ""
                val level = block.content["level"] as? Int ?: 1
                "<h$level class=\"$baseClass\"$blockStyle>${escapeHtml(text)}</h$level>"
            }

            BlockType.PARAGRAPH -> {
                val text = block.content["text"] as? String ?: ""
                "<p class=\"$baseClass\"$blockStyle>${escapeHtml(text)}</p>"
            }

            BlockType.IMAGE -> {
                val src = block.content["src"] as? String ?: ""
                val alt = block.content["alt"] as? String ?: ""
                val caption = block.content["caption"] as? String
                buildString {
                    append("<div class=\"$baseClass\"$blockStyle>")
                    append("<img src=\"${escapeHtml(src)}\" alt=\"${escapeHtml(alt)}\" style=\"max-width: 100%; height: auto;\">")
                    caption?.let {
                        append("<p class=\"image-caption\" style=\"margin-top: 8px; font-size: 0.9em; color: #666;\">${escapeHtml(it)}</p>")
                    }
                    append("</div>")
                }
            }

            BlockType.BUTTON -> {
                val text = block.content["text"] as? String ?: "Button"
                val href = block.content["href"] as? String ?: "#"
                val target = if (block.content["newTab"] as? Boolean == true) " target=\"_blank\"" else ""
                "<a href=\"${escapeHtml(href)}\" class=\"$baseClass btn\"$target$blockStyle>${escapeHtml(text)}</a>"
            }

            BlockType.LIST -> {
                val items = block.content["items"] as? List<*> ?: emptyList<String>()
                val ordered = block.content["ordered"] as? Boolean ?: false
                val tag = if (ordered) "ol" else "ul"
                buildString {
                    append("<$tag class=\"$baseClass\"$blockStyle>")
                    items.forEach { item ->
                        append("<li>${escapeHtml(item.toString())}</li>")
                    }
                    append("</$tag>")
                }
            }

            BlockType.SPACER -> {
                val height = block.content["height"] as? String ?: "20px"
                "<div class=\"$baseClass\" style=\"height: $height;\"></div>"
            }

            BlockType.DIVIDER -> {
                val color = block.content["color"] as? String ?: "#e0e0e0"
                val thickness = block.content["thickness"] as? String ?: "1px"
                "<hr class=\"$baseClass\" style=\"border: none; border-top: $thickness solid $color; margin: 20px 0;\"$blockStyle>"
            }
        }
    }

    /**
     * Generate CSS class for section type
     */
    private fun getSectionClass(type: SectionType): String = type.name.lowercase().replace("_", "-")

    /**
     * Generate inline style for section settings
     */
    private fun generateSectionStyle(settings: SectionSettings): String {
        val styles = mutableListOf<String>()

        settings.backgroundColor?.let {
            styles.add("background-color: $it")
        }

        val padding = formatSpacing(settings.padding)
        if (padding.isNotEmpty()) {
            styles.add("padding: $padding")
        }

        val margin = formatSpacing(settings.margin)
        if (margin.isNotEmpty()) {
            styles.add("margin: $margin")
        }

        return if (styles.isNotEmpty()) " style=\"${styles.joinToString("; ")}\"" else ""
    }

    /**
     * Generate inline style for block styling
     */
    private fun generateBlockStyle(styling: BlockStyling): String {
        val styles = mutableListOf<String>()

        styling.color?.let {
            styles.add("color: $it")
        }

        styling.backgroundColor?.let {
            styles.add("background-color: $it")
        }

        styling.fontSize?.let {
            styles.add("font-size: $it")
        }

        styling.fontWeight?.let {
            styles.add("font-weight: $it")
        }

        if (styling.textAlign.name != "LEFT") {
            styles.add("text-align: ${styling.textAlign.name.lowercase()}")
        }

        val padding = formatSpacing(styling.padding)
        if (padding.isNotEmpty()) {
            styles.add("padding: $padding")
        }

        val margin = formatSpacing(styling.margin)
        if (margin.isNotEmpty()) {
            styles.add("margin: $margin")
        }

        styling.borderRadius?.let {
            styles.add("border-radius: $it")
        }

        return if (styles.isNotEmpty()) " style=\"${styles.joinToString("; ")}\"" else ""
    }

    /**
     * Format spacing object to CSS value
     */
    private fun formatSpacing(spacing: Spacing): String {
        if (spacing.top == "0" && spacing.right == "0" && spacing.bottom == "0" && spacing.left == "0") {
            return ""
        }

        return if (spacing.top == spacing.right && spacing.right == spacing.bottom && spacing.bottom == spacing.left) {
            spacing.top
        } else if (spacing.top == spacing.bottom && spacing.left == spacing.right) {
            "${spacing.top} ${spacing.right}"
        } else {
            "${spacing.top} ${spacing.right} ${spacing.bottom} ${spacing.left}"
        }
    }

    /**
     * Get base URL for tenant
     */
    private fun getBaseUrl(tenant: Tenant): String =
        tenant.settings.customDomain?.let { "https://$it" }
            ?: "https://${tenant.subdomain}.example.com"

    /**
     * Escape HTML characters
     */
    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
}
