# Environment Variable Setup

## Overview

This document explains how to configure environment variables for the ImpactLens application, particularly for the frontend to connect to the backend API.

## Frontend Environment Variables

### Configuration Files

1. **`.env`** - Environment variables for development
2. **`.env.production`** - Environment variables for production (optional)
3. **`vite-env.d.ts`** - TypeScript type definitions for environment variables

### Current Setup

#### `.env` File
```bash
VITE_API_BASE_URL=http://localhost:8080
```

#### `vite-env.d.ts` File
```typescript
/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
```

#### `tsconfig.json` Include
```json
{
  "include": ["src", "vite.config.ts", "vite-env.d.ts"]
}
```

## Environment Variables Reference

### VITE_API_BASE_URL
- **Purpose**: Base URL for the backend API
- **Default**: `http://localhost:8080`
- **Usage**: Used in `src/lib/api.ts` for API client configuration
- **Example**: `http://localhost:8080` (development) or `https://api.impactlens.com` (production)

## Vite Environment Variable Rules

### Naming Convention
- All environment variables must be prefixed with `VITE_`
- Only variables with this prefix are exposed to the client-side code
- Example: `VITE_API_BASE_URL`, `VITE_APP_NAME`

### Access Pattern
```typescript
// Correct way to access Vite environment variables
const apiUrl = import.meta.env.VITE_API_BASE_URL

// ❌ Wrong - won't work in Vite
const apiUrl = process.env.VITE_API_BASE_URL
```

## Troubleshooting

### Environment Variable Not Picked Up

1. **Check File Location**
   - Ensure `.env` file is in the frontend root directory
   - File should be at the same level as `package.json`

2. **Check Variable Name**
   - Must start with `VITE_`
   - Case sensitive
   - No spaces around `=`

3. **Check TypeScript Configuration**
   - Ensure `vite-env.d.ts` is included in `tsconfig.json`
   - Restart TypeScript server if using VS Code

4. **Restart Development Server**
   ```bash
   npm run dev
   ```

### Common Issues

#### Issue: "Property 'env' does not exist on type 'ImportMeta'"
**Solution**: 
1. Create `vite-env.d.ts` file in frontend root
2. Add it to `tsconfig.json` include array
3. Restart TypeScript server

#### Issue: Environment variable is undefined
**Solution**:
1. Check variable name starts with `VITE_`
2. Restart development server
3. Clear browser cache

#### Issue: Different values in development vs production
**Solution**:
1. Create `.env.production` for production values
2. Use `import.meta.env.MODE` to check environment
3. Set variables in deployment platform (Vercel, Netlify, etc.)

## Development vs Production

### Development
```bash
# .env
VITE_API_BASE_URL=http://localhost:8080
```

### Production
```bash
# .env.production (optional)
VITE_API_BASE_URL=https://api.impactlens.com
```

### Docker Environment
```yaml
# docker-compose.yml
environment:
  VITE_API_BASE_URL: http://backend:8080
```

## Security Considerations

### Client-Side Exposure
- All `VITE_` prefixed variables are exposed to the browser
- Never include sensitive information (API keys, passwords)
- Use for public configuration only

### Sensitive Data
- Backend API keys should be in backend `.env` files
- Database credentials should be in backend configuration
- JWT secrets should be in backend environment

## Best Practices

1. **Use Descriptive Names**
   ```bash
   VITE_API_BASE_URL=http://localhost:8080
   VITE_APP_NAME=ImpactLens
   VITE_VERSION=1.0.0
   ```

2. **Provide Defaults**
   ```typescript
   const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
   ```

3. **Type Safety**
   ```typescript
   interface ImportMetaEnv {
     readonly VITE_API_BASE_URL: string
     readonly VITE_APP_NAME: string
   }
   ```

4. **Environment-Specific Files**
   - `.env` - Development defaults
   - `.env.local` - Local overrides (git ignored)
   - `.env.production` - Production values

## Verification

### Check Environment Variables
```typescript
// Add this to your component to debug
console.log('Environment:', import.meta.env)
console.log('API URL:', import.meta.env.VITE_API_BASE_URL)
```

### Test API Connection
```typescript
// Test API connection
const testConnection = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/actuator/health`)
    console.log('API Connection:', response.ok)
  } catch (error) {
    console.error('API Connection Failed:', error)
  }
}
```

## Deployment Platforms

### Vercel
```bash
# Set in Vercel dashboard
VITE_API_BASE_URL=https://your-api.vercel.app
```

### Netlify
```bash
# Set in Netlify dashboard
VITE_API_BASE_URL=https://your-api.netlify.app
```

### Docker
```dockerfile
# Dockerfile
ENV VITE_API_BASE_URL=http://backend:8080
```

## Debugging Commands

```bash
# Check environment variables
npm run dev

# Build with environment check
npm run build

# Type check
npx tsc --noEmit

# Lint
npm run lint
``` 