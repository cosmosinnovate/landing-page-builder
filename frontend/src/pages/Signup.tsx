import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { UserPlus, Mail, Lock, User, Building } from 'lucide-react';
import { apiClient } from '@/services/api';
import { slugify } from '@/utils/helpers';
import toast from 'react-hot-toast';

const Signup: React.FC = () => {
  const navigate = useNavigate();
  const { signup } = useAuth();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    subdomain: '',
    tenantName: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [subdomainChecking, setSubdomainChecking] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    // Auto-generate subdomain from tenant name
    if (name === 'tenantName') {
      const slugifiedSubdomain = slugify(value);
      setFormData((prev) => ({ ...prev, subdomain: slugifiedSubdomain }));
    }
  };

  const checkSubdomainAvailability = async () => {
    if (!formData.subdomain) return;

    setSubdomainChecking(true);
    try {
      const available = await apiClient.checkSubdomainAvailability(formData.subdomain);
      if (!available) {
        toast.error('This subdomain is already taken');
      }
    } catch (error) {
      console.error('Error checking subdomain:', error);
    } finally {
      setSubdomainChecking(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    if (formData.password.length < 8) {
      toast.error('Password must be at least 8 characters');
      return;
    }

    setIsLoading(true);

    try {
      const { confirmPassword, ...signupData } = formData;
      await signup(signupData);
      navigate('/dashboard');
    } catch (error) {
      // Error handling is done in AuthContext
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 to-primary-100 px-4 py-12">
      <div className="max-w-2xl w-full space-y-8 bg-white p-8 rounded-2xl shadow-xl">
        <div className="text-center">
          <div className="flex justify-center mb-4">
            <div className="bg-primary-600 p-3 rounded-full">
              <UserPlus className="w-8 h-8 text-white" />
            </div>
          </div>
          <h2 className="text-3xl font-bold text-gray-900">Create your account</h2>
          <p className="mt-2 text-gray-600">Get started with your landing page builder</p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label htmlFor="firstName" className="label">
                First Name
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="firstName"
                  name="firstName"
                  type="text"
                  required
                  value={formData.firstName}
                  onChange={handleChange}
                  className="input pl-10"
                  placeholder="John"
                />
              </div>
            </div>

            <div>
              <label htmlFor="lastName" className="label">
                Last Name
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="lastName"
                  name="lastName"
                  type="text"
                  required
                  value={formData.lastName}
                  onChange={handleChange}
                  className="input pl-10"
                  placeholder="Doe"
                />
              </div>
            </div>
          </div>

          <div>
            <label htmlFor="email" className="label">
              Email address
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Mail className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="input pl-10"
                placeholder="you@example.com"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label htmlFor="password" className="label">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  value={formData.password}
                  onChange={handleChange}
                  className="input pl-10"
                  placeholder="••••••••"
                />
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="label">
                Confirm Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="input pl-10"
                  placeholder="••••••••"
                />
              </div>
            </div>
          </div>

          <div>
            <label htmlFor="tenantName" className="label">
              Company / Organization Name
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Building className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="tenantName"
                name="tenantName"
                type="text"
                required
                value={formData.tenantName}
                onChange={handleChange}
                className="input pl-10"
                placeholder="Acme Corporation"
              />
            </div>
          </div>

          <div>
            <label htmlFor="subdomain" className="label">
              Subdomain
            </label>
            <div className="relative">
              <input
                id="subdomain"
                name="subdomain"
                type="text"
                required
                value={formData.subdomain}
                onChange={handleChange}
                onBlur={checkSubdomainAvailability}
                className="input pr-32"
                placeholder="your-site"
              />
              <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none text-gray-500">
                {subdomainChecking ? '...' : '.example.com'}
              </div>
            </div>
            <p className="mt-1 text-sm text-gray-500">
              Your site will be available at {formData.subdomain || 'your-site'}.example.com
            </p>
          </div>

          <div>
            <button
              type="submit"
              disabled={isLoading}
              className="w-full btn-primary py-3 text-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? 'Creating account...' : 'Create account'}
            </button>
          </div>

          <div className="text-center text-sm">
            <span className="text-gray-600">Already have an account? </span>
            <Link to="/login" className="text-primary-600 hover:text-primary-700 font-medium">
              Sign in
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Signup;

