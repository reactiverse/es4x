module.exports = {

  base: '/es4x/',

  head: [
    ['link', {rel: "shortcut icon", type: "image/png", href: "/favicon.png"}]
  ],

  // look at: https://github.com/vuejs/vuepress/blob/0.x/docs/.vuepress/config.js
  // for info on how to translate to other languages

  locales: {
    // The key is the path for the locale to be nested under.
    // As a special case, the default locale can use '/' as its path.
    '/': {
      lang: 'en-US', // this will be set as the lang attribute on <html>
      title: 'ES for Eclipse Vert.x',
      description: 'A Modern JavaScript runtime for Eclipse Vert.x'
    },
    '/zh/': {
      lang: 'zh-CN',
      title: '???',
      description: '???'
    },
    '/pt/': {
      lang: 'pt-PT',
      title: 'ES for Eclipse Vert.x',
      description: 'Um runtime JavaScript moderno para Eclipse Vert.x'
    }
  },
  serviceWorker: true,

  themeConfig: {
    repo: 'reactiverse/es4x',
    editLinks: true,
    docsDir: 'docs',
    docsBranch: 'develop',
    locales: {
      '/': {
        label: 'English',
        selectText: 'Languages',
        editLinkText: 'Edit this page on GitHub',
        lastUpdated: 'Last Updated',
        serviceWorker: {
          updatePopup: {
            message: "New content is available.",
            buttonText: "Refresh"
          }
        },
        nav: [
          {
            text: 'Get started',
            link: '/get-started/',
          },
          {
            text: 'Advanced',
            link: '/advanced/'
          },
          {
            text: 'API reference',
            link: '/api/'
          },
          {
            text: 'Changelog',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/get-started/': [
            {
              title: 'Getting Started',
              collapsable: false,
              children: [
                '',
                'install',
                'hello-world',
                'run',
                'test',
                'debug',
                'package',
                'shell',
                'contributing',
                'license'
              ]
            }
          ],
          '/advanced/': [
            {
              title: 'Advanced',
              collapsable: false,
              children: [
                '',
                'async-errors',
                'worker',
                'graphql'
              ]
            }
          ],
          '/api/': [
            {
              title: 'API',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/pt/': {
        label: 'Português',
        selectText: 'Linguagens',
        editLinkText: 'Editar esta página no GitHub',
        lastUpdated: 'Última Actualização',
        serviceWorker: {
          updatePopup: {
            message: "Novo conteúdo disponível.",
            buttonText: "Refrescar"
          }
        },
        nav: [
          {
            text: 'Introdução',
            link: '/get-started/',
          },
          {
            text: 'Avançado',
            link: '/advanced/'
          },
          {
            text: 'Referência da API',
            link: '/api/'
          },
          {
            text: 'Changelog',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/get-started/': [
            {
              title: 'Introdução',
              collapsable: false,
              children: [
                '',
                'install',
                'hello-world',
                'run',
                'test',
                'debug',
                'package',
                'shell',
                'contributing',
                'license'
              ]
            }
          ],
          '/advanced/': [
            {
              title: 'Avançado',
              collapsable: false,
              children: [
                '',
                'async-errors',
                'worker',
                'graphql'
              ]
            }
          ],
          '/api/': [
            {
              title: 'API',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      }

    }
  }
};
