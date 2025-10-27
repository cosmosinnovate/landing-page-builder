import { BlockType, SectionType, TextAlign } from '@/types';
import type { ContentBlock, ContentSection, Spacing, BlockStyling, SectionSettings } from '@/types';

export const generateId = (): string => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

export const createDefaultSpacing = (): Spacing => ({
  top: '0',
  right: '0',
  bottom: '0',
  left: '0',
});

export const createDefaultBlockStyling = (): BlockStyling => ({
  textAlign: TextAlign.LEFT,
  fontSize: null,
  fontWeight: null,
  color: null,
  backgroundColor: null,
  padding: createDefaultSpacing(),
  margin: createDefaultSpacing(),
  borderRadius: null,
  customCss: null,
});

export const createDefaultSectionSettings = (): SectionSettings => ({
  backgroundColor: null,
  padding: {
    top: '2rem',
    right: '1rem',
    bottom: '2rem',
    left: '1rem',
  },
  margin: createDefaultSpacing(),
  fullWidth: false,
  customCss: null,
});

export const createBlock = (type: BlockType, content: Record<string, any> = {}): ContentBlock => ({
  id: generateId(),
  type,
  content,
  styling: createDefaultBlockStyling(),
});

export const createSection = (type: SectionType, blocks: ContentBlock[] = []): ContentSection => ({
  id: generateId(),
  type,
  blocks,
  settings: createDefaultSectionSettings(),
});

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
};

export const slugify = (text: string): string => {
  return text
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/[\s_-]+/g, '-')
    .replace(/^-+|-+$/g, '');
};

export const classNames = (...classes: (string | boolean | undefined | null)[]): string => {
  return classes.filter(Boolean).join(' ');
};

