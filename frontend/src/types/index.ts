// User and Auth Types
export enum UserRole {
  OWNER = 'OWNER',
  ADMIN = 'ADMIN',
  EDITOR = 'EDITOR',
  VIEWER = 'VIEWER',
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  tenantId: string;
  role: UserRole;
  status: UserStatus;
  emailVerified: boolean;
  lastLoginAt: string | null;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface SignupRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  subdomain: string;
  tenantName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

// Tenant Types
export enum TenantStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
}

export interface TenantSettings {
  customDomain: string | null;
  logoUrl: string | null;
  faviconUrl: string | null;
  primaryColor: string;
  secondaryColor: string;
  fontFamily: string;
  analyticsId: string | null;
  customCss: string | null;
  customJs: string | null;
}

export interface Tenant {
  id: string;
  name: string;
  subdomain: string;
  status: TenantStatus;
  settings: TenantSettings;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
}

// Page Types
export enum PageStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED',
}

export enum SectionType {
  HEADER = 'HEADER',
  HERO = 'HERO',
  CONTENT = 'CONTENT',
  FEATURES = 'FEATURES',
  TESTIMONIALS = 'TESTIMONIALS',
  CTA = 'CTA',
  FOOTER = 'FOOTER',
}

export enum BlockType {
  HEADING = 'HEADING',
  PARAGRAPH = 'PARAGRAPH',
  IMAGE = 'IMAGE',
  BUTTON = 'BUTTON',
  LIST = 'LIST',
  SPACER = 'SPACER',
  DIVIDER = 'DIVIDER',
}

export enum TextAlign {
  LEFT = 'LEFT',
  CENTER = 'CENTER',
  RIGHT = 'RIGHT',
  JUSTIFY = 'JUSTIFY',
}

export interface Spacing {
  top: string;
  right: string;
  bottom: string;
  left: string;
}

export interface BlockStyling {
  textAlign: TextAlign;
  fontSize: string | null;
  fontWeight: string | null;
  color: string | null;
  backgroundColor: string | null;
  padding: Spacing;
  margin: Spacing;
  borderRadius: string | null;
  customCss: string | null;
}

export interface ContentBlock {
  id: string;
  type: BlockType;
  content: Record<string, any>;
  styling: BlockStyling;
}

export interface SectionSettings {
  backgroundColor: string | null;
  padding: Spacing;
  margin: Spacing;
  fullWidth: boolean;
  customCss: string | null;
}

export interface ContentSection {
  id: string;
  type: SectionType;
  blocks: ContentBlock[];
  settings: SectionSettings;
}

export interface DesignSettings {
  theme: string;
  primaryColor: string;
  secondaryColor: string;
  fontFamily: string;
  containerWidth: string;
  customCss: string | null;
}

export interface PageContent {
  sections: ContentSection[];
  designSettings: DesignSettings;
}

export interface SeoSettings {
  ogTitle: string | null;
  ogDescription: string | null;
  ogImage: string | null;
  twitterCard: string;
  canonicalUrl: string | null;
  noIndex: boolean;
  noFollow: boolean;
}

export interface Page {
  id: string;
  tenantId: string;
  slug: string;
  title: string;
  metaDescription: string | null;
  metaKeywords: string | null;
  status: PageStatus;
  content: PageContent;
  seoSettings: SeoSettings;
  createdAt: string;
  updatedAt: string;
  publishedAt: string | null;
}

export interface CreatePageRequest {
  slug: string;
  title: string;
  metaDescription?: string;
  metaKeywords?: string;
  status?: PageStatus;
  content?: PageContent;
  seoSettings?: SeoSettings;
}

export interface UpdatePageRequest {
  slug: string;
  title: string;
  metaDescription?: string;
  metaKeywords?: string;
  status?: PageStatus;
  content?: PageContent;
  seoSettings?: SeoSettings;
}

