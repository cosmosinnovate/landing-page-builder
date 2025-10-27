import React from 'react';
import { X } from 'lucide-react';
import { Page, BlockType, SectionType } from '@/types';
import type { ContentBlock, ContentSection } from '@/types';

interface PagePreviewProps {
  page: Page;
  onClose: () => void;
}

const PagePreview: React.FC<PagePreviewProps> = ({ page, onClose }) => {
  const renderBlock = (block: ContentBlock) => {
    const { styling } = block;
    const baseStyle = {
      textAlign: styling.textAlign.toLowerCase() as any,
      fontSize: styling.fontSize || undefined,
      fontWeight: styling.fontWeight || undefined,
      color: styling.color || undefined,
      backgroundColor: styling.backgroundColor || undefined,
      padding: `${styling.padding.top} ${styling.padding.right} ${styling.padding.bottom} ${styling.padding.left}`,
      margin: `${styling.margin.top} ${styling.margin.right} ${styling.margin.bottom} ${styling.margin.left}`,
      borderRadius: styling.borderRadius || undefined,
    };

    switch (block.type) {
      case BlockType.HEADING:
        const HeadingTag = `h${block.content.level || 2}` as keyof JSX.IntrinsicElements;
        return (
          <HeadingTag
            style={baseStyle}
            className={`font-bold ${
              block.content.level === 1
                ? 'text-4xl md:text-5xl mb-6'
                : block.content.level === 2
                ? 'text-3xl md:text-4xl mb-4'
                : block.content.level === 3
                ? 'text-2xl md:text-3xl mb-3'
                : 'text-xl md:text-2xl mb-2'
            }`}
          >
            {block.content.text || 'Heading'}
          </HeadingTag>
        );

      case BlockType.PARAGRAPH:
        return (
          <p style={baseStyle} className="text-base md:text-lg leading-relaxed mb-4">
            {block.content.text || 'Paragraph text...'}
          </p>
        );

      case BlockType.IMAGE:
        if (!block.content.src) {
          return (
            <div style={baseStyle} className="bg-gray-200 rounded-lg flex items-center justify-center h-64 mb-4">
              <span className="text-gray-400">Image placeholder</span>
            </div>
          );
        }
        return (
          <img
            src={block.content.src}
            alt={block.content.alt || ''}
            style={{ ...baseStyle, width: block.content.width || '100%' }}
            className="rounded-lg mb-4"
          />
        );

      case BlockType.BUTTON:
        return (
          <a
            href={block.content.url || '#'}
            style={baseStyle}
            className={`inline-block px-8 py-3 rounded-lg font-semibold transition-all mb-4 ${
              block.content.variant === 'primary'
                ? 'bg-blue-600 text-white hover:bg-blue-700'
                : 'bg-gray-200 text-gray-900 hover:bg-gray-300'
            }`}
          >
            {block.content.text || 'Button'}
          </a>
        );

      case BlockType.LIST:
        const ListTag = block.content.ordered ? 'ol' : 'ul';
        return (
          <ListTag
            style={baseStyle}
            className={`mb-4 ${block.content.ordered ? 'list-decimal' : 'list-disc'} list-inside space-y-2`}
          >
            {(block.content.items || []).map((item: string, index: number) => (
              <li key={index} className="text-base md:text-lg">
                {item}
              </li>
            ))}
          </ListTag>
        );

      case BlockType.SPACER:
        return <div style={{ height: block.content.height || '2rem' }} />;

      case BlockType.DIVIDER:
        return (
          <hr
            style={{
              borderColor: block.content.color || '#e5e7eb',
              borderStyle: block.content.style || 'solid',
              margin: '2rem 0',
            }}
          />
        );

      default:
        return null;
    }
  };

  const renderSection = (section: ContentSection) => {
    const { settings } = section;
    const sectionStyle = {
      backgroundColor: settings.backgroundColor || getSectionDefaultBg(section.type),
      padding: `${settings.padding.top} ${settings.padding.right} ${settings.padding.bottom} ${settings.padding.left}`,
      margin: `${settings.margin.top} ${settings.margin.right} ${settings.margin.bottom} ${settings.margin.left}`,
    };

    const containerClass = settings.fullWidth ? 'w-full' : 'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8';

    return (
      <section key={section.id} style={sectionStyle} className="relative">
        <div className={containerClass}>
          {section.blocks.map((block) => (
            <div key={block.id}>{renderBlock(block)}</div>
          ))}
        </div>
      </section>
    );
  };

  const getSectionDefaultBg = (type: SectionType): string => {
    switch (type) {
      case SectionType.HERO:
        return '#f9fafb';
      case SectionType.FOOTER:
        return '#1f2937';
      case SectionType.CTA:
        return '#3b82f6';
      default:
        return '#ffffff';
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-2xl w-full max-w-7xl h-[90vh] flex flex-col">
        {/* Preview Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b bg-gray-50">
          <div>
            <h2 className="text-xl font-bold text-gray-900">Preview: {page.title}</h2>
            <p className="text-sm text-gray-600">
              This is how your page will look when published
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-200 rounded-lg transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Preview Content */}
        <div className="flex-1 overflow-y-auto bg-white">
          <div
            style={{
              fontFamily: page.content.designSettings.fontFamily,
              maxWidth: page.content.designSettings.containerWidth,
              margin: '0 auto',
            }}
          >
            {/* Page Title (Meta) */}
            <div className="bg-gray-100 px-4 py-2 text-center border-b">
              <div className="text-sm text-gray-600">
                <strong>Title:</strong> {page.title} | <strong>Slug:</strong> /{page.slug}
              </div>
              {page.metaDescription && (
                <div className="text-xs text-gray-500 mt-1">
                  <strong>Description:</strong> {page.metaDescription}
                </div>
              )}
            </div>

            {/* Render Sections */}
            {page.content.sections.map((section) => renderSection(section))}
          </div>
        </div>

        {/* Preview Footer */}
        <div className="px-6 py-4 border-t bg-gray-50 flex justify-between items-center">
          <div className="flex space-x-4 text-sm text-gray-600">
            <span>Status: <strong>{page.status}</strong></span>
            <span>•</span>
            <span>Sections: <strong>{page.content.sections.length}</strong></span>
          </div>
          <button onClick={onClose} className="btn-primary">
            Close Preview
          </button>
        </div>
      </div>
    </div>
  );
};

export default PagePreview;

