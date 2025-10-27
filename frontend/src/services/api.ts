import axios, { AxiosError, AxiosInstance } from 'axios';
import type {
  AuthResponse,
  LoginRequest,
  SignupRequest,
  Tenant,
  Page,
  CreatePageRequest,
  UpdatePageRequest,
  PageStatus,
} from '@/types';

const API_BASE_URL = '/api/v1';

class ApiClient {
  private client: AxiosInstance;
  private refreshPromise: Promise<string> | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle token refresh
    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as any;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const newToken = await this.refreshAccessToken();
            if (newToken && originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${newToken}`;
              return this.client(originalRequest);
            }
          } catch (refreshError) {
            // Refresh failed, redirect to login
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private async refreshAccessToken(): Promise<string> {
    // Prevent multiple simultaneous refresh requests
    if (this.refreshPromise) {
      return this.refreshPromise;
    }

    this.refreshPromise = (async () => {
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const response = await axios.post<AuthResponse>(
          `${API_BASE_URL}/auth/refresh`,
          { refreshToken }
        );

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        return accessToken;
      } finally {
        this.refreshPromise = null;
      }
    })();

    return this.refreshPromise;
  }

  // Auth APIs
  async signup(data: SignupRequest): Promise<AuthResponse> {
    const response = await this.client.post<AuthResponse>('/auth/signup', data);
    return response.data;
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await this.client.post<AuthResponse>('/auth/login', data);
    return response.data;
  }

  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    const response = await this.client.post<AuthResponse>('/auth/refresh', {
      refreshToken,
    });
    return response.data;
  }

  // Tenant APIs
  async getTenant(tenantId: string): Promise<Tenant> {
    const response = await this.client.get<Tenant>(`/tenants/${tenantId}`);
    return response.data;
  }

  async updateTenant(tenantId: string, data: Partial<Tenant>): Promise<Tenant> {
    const response = await this.client.put<Tenant>(`/tenants/${tenantId}`, data);
    return response.data;
  }

  async checkSubdomainAvailability(subdomain: string): Promise<boolean> {
    const response = await this.client.get<{ subdomain: string; available: boolean }>(
      `/tenants/subdomain/${subdomain}/availability`
    );
    return response.data.available;
  }

  // Page APIs
  async getPages(tenantId: string): Promise<Page[]> {
    const response = await this.client.get<Page[]>(`/pages/tenant/${tenantId}`);
    return response.data;
  }

  async getPagesByStatus(tenantId: string, status: PageStatus): Promise<Page[]> {
    const response = await this.client.get<Page[]>(
      `/pages/tenant/${tenantId}/status/${status}`
    );
    return response.data;
  }

  async getPage(pageId: string): Promise<Page> {
    const response = await this.client.get<Page>(`/pages/${pageId}`);
    return response.data;
  }

  async createPage(tenantId: string, data: CreatePageRequest): Promise<Page> {
    const payload = {
      ...data,
      tenantId,
    };
    console.log('API: Creating page with payload:', payload);
    const response = await this.client.post<Page>('/pages', payload);
    console.log('API: Page created successfully:', response.data);
    return response.data;
  }

  async updatePage(pageId: string, data: UpdatePageRequest): Promise<Page> {
    console.log('API: Updating page:', pageId, 'with data:', data);
    const response = await this.client.put<Page>(`/pages/${pageId}`, data);
    console.log('API: Page updated successfully:', response.data);
    return response.data;
  }

  async deletePage(pageId: string): Promise<void> {
    await this.client.delete(`/pages/${pageId}`);
  }

  async publishPage(pageId: string): Promise<Page> {
    const response = await this.client.patch<Page>(`/pages/${pageId}/publish`);
    return response.data;
  }

  async unpublishPage(pageId: string): Promise<Page> {
    const response = await this.client.patch<Page>(`/pages/${pageId}/unpublish`);
    return response.data;
  }

  async checkSlugAvailability(tenantId: string, slug: string): Promise<boolean> {
    const response = await this.client.get<{ available: boolean }>(
      `/pages/tenant/${tenantId}/slug/${slug}/availability`
    );
    return response.data.available;
  }
}

export const apiClient = new ApiClient();

