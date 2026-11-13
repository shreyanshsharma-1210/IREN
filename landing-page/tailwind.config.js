/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        graphite: {
          DEFAULT: '#171B19',
          surface: '#1E2320',
          elevated: '#252B28',
          border: 'rgba(234, 231, 223, 0.12)',
          muted: '#8A928C',
        },
        porcelain: {
          DEFAULT: '#EAE7DF',
          dark: '#E1DDD3',
          light: '#F1EFE8',
        },
        sage: {
          DEFAULT: '#879487',
          dark: '#6E7A6E',
          deep: '#576357',
          light: '#9EAA9E',
          subtle: 'rgba(135, 148, 135, 0.14)',
          border: 'rgba(135, 148, 135, 0.28)',
        },
        champagne: {
          DEFAULT: '#B69A75',
          dark: '#9A805E',
          light: '#C9B294',
          subtle: 'rgba(182, 154, 117, 0.14)',
          border: 'rgba(182, 154, 117, 0.3)',
        },
        ivory: {
          DEFAULT: '#F7F5EF',
          pure: '#FBF9F4',
          card: '#F6F3EB',
          border: '#DED9CE',
          borderSubtle: '#E8E4DA',
        },
      },
      fontFamily: {
        serif: ['"Instrument Serif"', 'Georgia', 'serif'],
        sans: ['"Plus Jakarta Sans"', 'Inter', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      letterSpacing: {
        tighter: '-0.035em',
        tight: '-0.02em',
        editorial: '0.04em',
      },
      boxShadow: {
        subtle: '0 1px 3px rgba(23, 27, 25, 0.04), 0 4px 12px rgba(23, 27, 25, 0.03)',
        card: '0 2px 8px rgba(23, 27, 25, 0.04), 0 12px 32px rgba(23, 27, 25, 0.05)',
        elevated: '0 10px 40px -10px rgba(23, 27, 25, 0.08), 0 1px 3px rgba(23, 27, 25, 0.04)',
        darkCard: '0 12px 36px -8px rgba(0, 0, 0, 0.45)',
      },
    },
  },
  plugins: [],
}
