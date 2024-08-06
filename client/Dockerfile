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

# Install all dependencies listed. However packages needed for production will be already installed due to above stage.
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile

# Copy all files to container.
COPY . .

EXPOSE 3000
CMD [ "pnpm", "dev" ]
