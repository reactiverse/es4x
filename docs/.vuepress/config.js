module.exports = {

  base: '/es4x/',

  head: [
    ['link', {rel: "shortcut icon", type: "image/png", href: "/favicon.png"}],
    ['link', {rel: "stylesheet", type: "text/css", href:"/player/asciinema-player.css"}],
    ['script', {src: "/player/asciinema-player.js"}]
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
    '/gr/': {
      lang: 'el-GR',
      title: 'ES για Eclipse Vert.x',
      description: 'Ένα σύγχρονο JavaScript runtime για το Eclipse Vert.x'
    },
    '/zh/': {
      lang: 'zh-CN',
      title: '基于Eclipse Vert.x的ES',
      description: '一款先进的基于Eclipse Vert.x的JavaScript运行时'
    },
    '/pt/': {
      lang: 'pt-PT',
      title: 'ES for Eclipse Vert.x',
      description: 'Um runtime JavaScript moderno para Eclipse Vert.x'
    },
    '/pl/': {
      lang: 'pl-PL',
      title: 'ES for Eclipse Vert.x',
      description: 'A Modern JavaScript runtime for Eclipse Vert.x'
    },
    '/ru/': {
      lang: 'ru-RU',
      title: 'ES for Eclipse Vert.x',
      description: 'A Modern JavaScript runtime for Eclipse Vert.x'
    },
  },
  serviceWorker: false,

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
            text: 'Examples',
            link: '/examples/'
          },
          {
            text: 'APIs',
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
          '/examples/': [
            {
              title: 'Examples',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/advanced/': [
            {
              title: 'Advanced',
              collapsable: false,
              children: [
                '',
                'commonjs',
                'esm',
                'globals',
                'vertx',
                'worker',
                'jars',
                'security',
                'logging'
              ]
            }
          ],
          '/api/': [
            {
              title: 'APIs',
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
            text: 'APIs',
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
                // 'commonjs',
                // 'esm',
                // 'vertx',
                // 'globals',
                // 'worker',
                // 'graphql'
              ]
            }
          ],
          '/api/': [
            {
              title: 'APIs',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/ru/': {
        label: 'Russian',
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
            link: '/ru/get-started/',
          },
          {
            text: 'Advanced',
            link: '/ru/advanced/'
          },
          {
            text: 'Examples',
            link: '/ru/examples/'
          },
          {
            text: 'APIs',
            link: '/ru/api/'
          },
          {
            text: 'Changelog',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/ru/get-started/': [
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
          '/ru/examples/': [
            {
              title: 'Examples',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/ru/advanced/': [
            {
              title: 'Advanced',
              collapsable: false,
              children: [
                '',
                'commonjs',
                'esm',
                'globals',
                'vertx',
                'worker',
                'jars',
                'security',
                'logging'
              ]
            }
          ],
          '/ru/api/': [
            {
              title: 'APIs',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/pl/': {
        label: 'Polish',
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
            link: '/pl/get-started/',
          },
          {
            text: 'Advanced',
            link: '/pl/advanced/'
          },
          {
            text: 'Examples',
            link: '/pl/examples/'
          },
          {
            text: 'APIs',
            link: '/pl/api/'
          },
          {
            text: 'Changelog',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/pl/get-started/': [
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
          '/pl/examples/': [
            {
              title: 'Examples',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/pl/advanced/': [
            {
              title: 'Advanced',
              collapsable: false,
              children: [
                '',
                'commonjs',
                'esm',
                'globals',
                'vertx',
                'worker',
                'jars',
                'security',
                'logging'
              ]
            }
          ],
          '/pl/api/': [
            {
              title: 'APIs',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/gr/': {
        label: 'Ελληνικά',
        selectText: 'Γλώσσες',
        editLinkText: 'Επεξεργαστείτε αυτήν τη σελίδα στο GitHub',
        lastUpdated: 'Τελευταία ενημέρωση',
        serviceWorker: {
          updatePopup: {
            message: "Νέο περιεχόμενο είναι διαθέσιμο.",
            buttonText: "Ανανέωση"
          }
        },
        nav: [
          {
            text: 'Έναρξη',
            link: '/gr/get-started/',
          },
          {
            text: 'Για προχωρημένους',
            link: '/gr/advanced/'
          },
          {
            text: 'Παραδείγματα',
            link: '/gr/examples/'
          },
          {
            text: 'APIs',
            link: '/gr/api/'
          },
          {
            text: 'Αρχείο καταγραφής αλλαγών',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/gr/get-started/': [
            {
              title: 'Έναρξη',
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
          '/gr/examples/': [
            {
              title: 'Παραδείγματα',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/gr/advanced/': [
            {
              title: 'Για προχωρημένους',
              collapsable: false,
              children: [
                '',
                'commonjs',
                'esm',
                'globals',
                'vertx',
                'worker',
                'jars',
                'security',
                'logging'
              ]
            }
          ],
          '/gr/api/': [
            {
              title: 'APIs',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/zh/': {
              label: '简体中文',
              selectText: '语言',
              editLinkText: '在Github上编辑此页面',
              lastUpdated: '上次更新',
              serviceWorker: {
                updatePopup: {
                  message: "有新内容发布。",
                  buttonText: "更新"
                }
              },
              nav: [
                {
                  text: '由此开始',
                  link: '/zh/get-started/',
                },
                {
                  text: '进阶内容',
                  link: '/zh/advanced/'
                },
                {
                  text: 'API参考',
                  link: '/api/'
                },
                {
                  text: '更新日志',
                  link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
                },
              ],
              sidebar: {
                '/zh/get-started/': [
                  {
                    title: '由此开始',
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
                '/zh/advanced/': [
                  {
                    title: '进阶内容',
                    collapsable: false,
                    children: [
                      '',
                      // 'commonjs',
                      // 'esm',
                      // 'vertx',
                      'globals',
                      'worker',
                      'graphql'
                    ]
                  }
                ],
                '/api/': [
                  {
                    title: 'API参考',
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
