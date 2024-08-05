# Get base image and give current stage alias 'base'
FROM node:20-slim AS base

# Set environment variable.
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"

# Enable corepack to use pnpm
RUN corepack enable
RUN pnpm config set store-dir /pnpm/store/v3

WORKDIR /fool-proof

# Copy package.json and pnpm-lock.yaml before 'pnpm install' for dependencies installation.
COPY package.json pnpm-lock.yaml ./

# Start a new build stage named 'prod-deps' based on the 'base' stage.
FROM base AS prod-deps
# Install dependencies listed only in 'dependencies' in package.json.
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --prod --frozen-lockfile

# Start a new build stage named 'build' based on the 'base' stage.
FROM base AS build
# Install all dependencies listed. However packages needed for production will be already installed due to above stage.
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile
RUN pnpm run build

FROM base
# Copy the node_modules directory from the prod-deps stage to the current stage.
# This will copy modules that are listed only in 'dependencies' in package.json
COPY --from=prod-deps /fool-proof/node_modules /fool-proof/node_modules

# Copy all files to container.
COPY . .

# Chage this when distributing.
EXPOSE 3000
CMD [ "pnpm", "dev" ]

# EXPOSE 8000
# CMD ["pnpm", "start"]