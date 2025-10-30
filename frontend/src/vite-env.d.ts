/// <reference types="vite/client" />

// Declare module types for CSS imports
declare module '*.css' {
  const content: Record<string, string>;
  export default content;
}

