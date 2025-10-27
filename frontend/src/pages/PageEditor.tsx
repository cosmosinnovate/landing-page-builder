import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { apiClient } from '@/services/api';
import { Page, PageStatus, ContentSection, BlockType } from '@/types';
import PageBuilder from '@/components/PageBuilder';
import PagePreview from '@/components/PagePreview';
import { ArrowLeft, Settings, Eye, Save } from 'lucide-react';
import toast from 'react-hot-toast';
import { createSection, createBlock } from '@/utils/helpers';
import { SectionType } from '@/types';

const PageEditor: React.FC = () => {
  const { pageId } = useParams<{ pageId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [page, setPage] = useState<Page | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const [showPreview, setShowPreview] = useState(false);

  useEffect(() => {
    if (pageId === 'new') {
      // Create a new page with a complete starter template
      const newPage: Partial<Page> = {
        title: 'New Landing Page',
        slug: 'new-page-' + Date.now(),
        status: PageStatus.DRAFT,
        tenantId: user?.tenantId || '',
        content: {
          sections: [
            // Header Section
            createSection(SectionType.HEADER, [
              createBlock(BlockType.HEADING, { text: 'Your Brand', level: 3 }),
            ]),
            
            // Hero Section
            createSection(SectionType.HERO, [
              createBlock(BlockType.HEADING, { text: 'Welcome to Your Landing Page', level: 1 }),
              createBlock(BlockType.PARAGRAPH, { 
                text: 'Create beautiful landing pages in minutes with our drag-and-drop builder. No coding required!' 
              }),
              createBlock(BlockType.BUTTON, { text: 'Get Started', url: '#', variant: 'primary' }),
            ]),
            
            // Features Section
            createSection(SectionType.FEATURES, [
              createBlock(BlockType.HEADING, { text: 'Key Features', level: 2 }),
              createBlock(BlockType.PARAGRAPH, { 
                text: 'Everything you need to create amazing landing pages' 
              }),
              createBlock(BlockType.SPACER, { height: '2rem' }),
              createBlock(BlockType.LIST, { 
                items: [
                  'Easy drag-and-drop interface',
                  'Beautiful pre-built components',
                  'Mobile-responsive design',
                  'SEO optimized',
                  'Fast loading times'
                ], 
                ordered: false 
              }),
            ]),
            
            // CTA Section
            createSection(SectionType.CTA, [
              createBlock(BlockType.HEADING, { text: 'Ready to Get Started?', level: 2 }),
              createBlock(BlockType.PARAGRAPH, { 
                text: 'Join thousands of satisfied customers building amazing landing pages' 
              }),
              createBlock(BlockType.BUTTON, { text: 'Start Building Now', url: '#', variant: 'primary' }),
            ]),
            
            // Footer Section
            createSection(SectionType.FOOTER, [
              createBlock(BlockType.PARAGRAPH, { text: '© 2024 Your Company. All rights reserved.' }),
            ]),
          ],
          designSettings: {
            theme: 'default',
            primaryColor: '#3b82f6',
            secondaryColor: '#6b7280',
            fontFamily: 'Inter, system-ui, sans-serif',
            containerWidth: '1200px',
            customCss: null,
          },
        },
        seoSettings: {
          ogTitle: null,
          ogDescription: null,
          ogImage: null,
          twitterCard: 'summary',
          canonicalUrl: null,
          noIndex: false,
          noFollow: false,
        },
      };
      setPage(newPage as Page);
      setIsLoading(false);
    } else if (pageId) {
      loadPage(pageId);
    }
  }, [pageId, user]);

  const loadPage = async (id: string) => {
    setIsLoading(true);
    try {
      const data = await apiClient.getPage(id);
      setPage(data);
    } catch (error) {
      toast.error('Failed to load page');
      console.error('Failed to load page:', error);
      navigate('/dashboard');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSave = async () => {
    if (!page || !user) return;

    console.log('Saving page...', { page, user });
    setIsSaving(true);
    try {
      if (page.id) {
        // Update existing page
        console.log('Updating existing page:', page.id);
        const updateData = {
          title: page.title,
          slug: page.slug,
          metaDescription: page.metaDescription || undefined,
          metaKeywords: page.metaKeywords || undefined,
          status: page.status,
          content: page.content,
          seoSettings: page.seoSettings,
        };
        console.log('Update data:', updateData);
        const updated = await apiClient.updatePage(page.id, { id: page.id, ...updateData });
        setPage(updated);
        toast.success('Page saved successfully');
      } else {
        // Create new page
        console.log('Creating new page for tenant:', user.tenantId);
        const createData = {
          title: page.title,
          slug: page.slug,
          metaDescription: page.metaDescription || undefined,
          metaKeywords: page.metaKeywords || undefined,
          status: page.status,
          content: page.content,
          seoSettings: page.seoSettings,
        };
        console.log('Create data:', createData);
        const created = await apiClient.createPage(user.tenantId, createData);
        setPage(created);
        toast.success('Page created successfully');
        navigate(`/pages/${created.id}/edit`, { replace: true });
      }
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || 'Failed to save page';
      toast.error(message);
      console.error('Failed to save page:', error);
      console.error('Error details:', {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message,
      });
    } finally {
      setIsSaving(false);
    }
  };

  const handlePreview = () => {
    setShowPreview(true);
  };

  const updateSections = (sections: ContentSection[]) => {
    if (page) {
      setPage({
        ...page,
        content: {
          ...page.content,
          sections,
        },
      });
    }
  };

  const updatePageField = (field: keyof Page, value: any) => {
    if (page) {
      setPage({ ...page, [field]: value });
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading page...</p>
        </div>
      </div>
    );
  }

  if (!page) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">Page not found</p>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col">
      {/* Top Bar */}
      <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/dashboard')}
            className="text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <input
              type="text"
              value={page.title}
              onChange={(e) => updatePageField('title', e.target.value)}
              className="text-lg font-semibold border-none focus:outline-none focus:ring-2 focus:ring-primary-500 rounded px-2 py-1"
              placeholder="Page Title"
            />
            <div className="flex items-center space-x-2 mt-1">
              <input
                type="text"
                value={page.slug}
                onChange={(e) => updatePageField('slug', e.target.value)}
                className="text-sm text-gray-600 border-none focus:outline-none focus:ring-2 focus:ring-primary-500 rounded px-2 py-1"
                placeholder="page-slug"
              />
              <span
                className={`px-2 py-1 text-xs font-medium rounded-full ${
                  page.status === PageStatus.PUBLISHED
                    ? 'bg-green-100 text-green-800'
                    : page.status === PageStatus.DRAFT
                    ? 'bg-yellow-100 text-yellow-800'
                    : 'bg-gray-100 text-gray-800'
                }`}
              >
                {page.status}
              </span>
            </div>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={() => setShowSettings(!showSettings)}
            className="btn-secondary flex items-center space-x-2"
          >
            <Settings className="w-4 h-4" />
            <span>Settings</span>
          </button>
          <button
            onClick={handlePreview}
            className="btn-secondary flex items-center space-x-2"
          >
            <Eye className="w-4 h-4" />
            <span>Preview</span>
          </button>
          <button
            onClick={handleSave}
            disabled={isSaving}
            className="btn-primary flex items-center space-x-2 disabled:opacity-50"
          >
            <Save className="w-4 h-4" />
            <span>{isSaving ? 'Saving...' : 'Save'}</span>
          </button>
        </div>
      </div>

      {/* Settings Panel */}
      {showSettings && (
        <div className="bg-yellow-50 border-b border-yellow-200 px-4 py-3">
          <div className="max-w-4xl mx-auto">
            <h3 className="text-sm font-semibold text-gray-900 mb-3">Page Settings</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="label text-xs">Meta Description</label>
                <textarea
                  value={page.metaDescription || ''}
                  onChange={(e) => updatePageField('metaDescription', e.target.value)}
                  className="input text-sm"
                  rows={2}
                  placeholder="Brief description for SEO"
                />
              </div>
              <div>
                <label className="label text-xs">Meta Keywords</label>
                <input
                  type="text"
                  value={page.metaKeywords || ''}
                  onChange={(e) => updatePageField('metaKeywords', e.target.value)}
                  className="input text-sm"
                  placeholder="keyword1, keyword2, keyword3"
                />
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Page Builder */}
      <div className="flex-1 overflow-hidden">
        <PageBuilder
          sections={page.content.sections}
          onChange={updateSections}
          onSave={handleSave}
          onPreview={handlePreview}
        />
      </div>

      {/* Preview Modal */}
      {showPreview && (
        <PagePreview page={page} onClose={() => setShowPreview(false)} />
      )}
    </div>
  );
};

export default PageEditor;

