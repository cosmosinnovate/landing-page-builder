import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { apiClient } from '@/services/api';
import { Page, PageStatus } from '@/types';
import {
  Plus,
  FileText,
  LogOut,
  Settings,
  Eye,
  Edit,
  Trash2,
  Globe,
  FileX,
} from 'lucide-react';
import toast from 'react-hot-toast';
import { formatDate } from '@/utils/helpers';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [pages, setPages] = useState<Page[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [filter, setFilter] = useState<PageStatus | 'ALL'>('ALL');

  useEffect(() => {
    loadPages();
  }, [user]);

  const loadPages = async () => {
    if (!user) return;

    setIsLoading(true);
    try {
      const data = await apiClient.getPages(user.tenantId);
      setPages(data);
    } catch (error) {
      toast.error('Failed to load pages');
      console.error('Failed to load pages:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeletePage = async (pageId: string) => {
    if (!confirm('Are you sure you want to delete this page?')) return;

    try {
      await apiClient.deletePage(pageId);
      toast.success('Page deleted successfully');
      loadPages();
    } catch (error) {
      toast.error('Failed to delete page');
      console.error('Failed to delete page:', error);
    }
  };

  const handlePublishToggle = async (page: Page) => {
    try {
      if (page.status === PageStatus.PUBLISHED) {
        await apiClient.unpublishPage(page.id);
        toast.success('Page unpublished');
      } else {
        await apiClient.publishPage(page.id);
        toast.success('Page published successfully');
      }
      loadPages();
    } catch (error) {
      toast.error('Failed to update page status');
      console.error('Failed to update page status:', error);
    }
  };

  const filteredPages =
    filter === 'ALL' ? pages : pages.filter((page) => page.status === filter);

  const getStatusBadge = (status: PageStatus) => {
    const badges = {
      [PageStatus.DRAFT]: 'bg-yellow-100 text-yellow-800',
      [PageStatus.PUBLISHED]: 'bg-green-100 text-green-800',
      [PageStatus.ARCHIVED]: 'bg-gray-100 text-gray-800',
    };
    return badges[status] || '';
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Landing Page Builder</h1>
              <p className="text-sm text-gray-600 mt-1">
                Welcome back, {user?.firstName} {user?.lastName}
              </p>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate('/settings')}
                className="btn-secondary flex items-center space-x-2"
              >
                <Settings className="w-4 h-4" />
                <span>Settings</span>
              </button>
              <button
                onClick={logout}
                className="btn-secondary flex items-center space-x-2"
              >
                <LogOut className="w-4 h-4" />
                <span>Logout</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Actions Bar */}
        <div className="flex justify-between items-center mb-6">
          <div className="flex space-x-2">
            <button
              onClick={() => setFilter('ALL')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === 'ALL'
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              All ({pages.length})
            </button>
            <button
              onClick={() => setFilter(PageStatus.PUBLISHED)}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === PageStatus.PUBLISHED
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              Published ({pages.filter((p) => p.status === PageStatus.PUBLISHED).length})
            </button>
            <button
              onClick={() => setFilter(PageStatus.DRAFT)}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === PageStatus.DRAFT
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              Drafts ({pages.filter((p) => p.status === PageStatus.DRAFT).length})
            </button>
          </div>
          <button
            onClick={() => navigate('/pages/new/edit')}
            className="btn-primary flex items-center space-x-2"
          >
            <Plus className="w-5 h-5" />
            <span>Create New Page</span>
          </button>
        </div>

        {/* Pages List */}
        {isLoading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading pages...</p>
          </div>
        ) : filteredPages.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm">
            <FileX className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No pages found</h3>
            <p className="text-gray-600 mb-6">
              {filter === 'ALL'
                ? 'Get started by creating your first landing page'
                : `No ${filter.toLowerCase()} pages found`}
            </p>
            <button
              onClick={() => navigate('/pages/new/edit')}
              className="btn-primary inline-flex items-center space-x-2"
            >
              <Plus className="w-5 h-5" />
              <span>Create Your First Page</span>
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredPages.map((page) => (
              <div
                key={page.id}
                className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow"
              >
                <div className="p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex items-center space-x-2">
                      <FileText className="w-5 h-5 text-primary-600" />
                      <h3 className="text-lg font-semibold text-gray-900 truncate">
                        {page.title}
                      </h3>
                    </div>
                    <span
                      className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusBadge(
                        page.status
                      )}`}
                    >
                      {page.status}
                    </span>
                  </div>

                  <p className="text-sm text-gray-600 mb-4 line-clamp-2">
                    {page.metaDescription || 'No description'}
                  </p>

                  <div className="text-xs text-gray-500 mb-4">
                    <p>Slug: /{page.slug || 'home'}</p>
                    <p>Updated: {formatDate(page.updatedAt)}</p>
                  </div>

                  <div className="flex space-x-2">
                    <button
                      onClick={() => navigate(`/pages/${page.id}/edit`)}
                      className="flex-1 btn-secondary flex items-center justify-center space-x-1 text-sm py-2"
                    >
                      <Edit className="w-4 h-4" />
                      <span>Edit</span>
                    </button>
                    <button
                      onClick={() => handlePublishToggle(page)}
                      className={`flex-1 flex items-center justify-center space-x-1 text-sm py-2 rounded-lg font-medium transition-colors ${
                        page.status === PageStatus.PUBLISHED
                          ? 'bg-gray-200 text-gray-900 hover:bg-gray-300'
                          : 'bg-green-600 text-white hover:bg-green-700'
                      }`}
                    >
                      {page.status === PageStatus.PUBLISHED ? (
                        <>
                          <Eye className="w-4 h-4" />
                          <span>Unpublish</span>
                        </>
                      ) : (
                        <>
                          <Globe className="w-4 h-4" />
                          <span>Publish</span>
                        </>
                      )}
                    </button>
                    <button
                      onClick={() => handleDeletePage(page.id)}
                      className="px-3 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;

