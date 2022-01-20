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
    '/es/': {
      lang: 'es-ES',
      title: 'ES para Eclipse Vert.x',
      description: 'Un JavaScript runtime moderno para Eclipse Vert.x'
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
      title: 'ES dla Eclipse Vert.x',
      description: 'Nowoczesny runtime JavaScript dla Eclipse Vert.x'
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
                'hot-reload',
                'worker',
                'jars',
                'security',
                'logging',
                'codegen'
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
        label: 'Русский',
        selectText: 'Языки',
        editLinkText: 'Редактируйте эту страницу на GitHub',
        lastUpdated: 'Последнее обновление',
        serviceWorker: {
          updatePopup: {
            message: "Новый контент доступен.",
            buttonText: "Обновить"
          }
        },
        nav: [
          {
            text: 'Как начать',
            link: '/ru/get-started/',
          },
          {
            text: 'Дополнительно',
            link: '/ru/advanced/'
          },
          {
            text: 'Примеры',
            link: '/ru/examples/'
          },
          {
            text: 'API',
            link: '/ru/api/'
          },
          {
            text: 'Список изменений',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/ru/get-started/': [
            {
              title: 'Как начать',
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
              title: 'Примеры',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/ru/advanced/': [
            {
              title: 'Дополнительно',
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
              title: 'API',
              collapsable: false,
              children: [
                ''
              ]
            }
          ]
        },
      },
      '/pl/': {
        label: 'Polski',
        selectText: 'Języki',
        editLinkText: 'Edytuj stronę na GitHubie',
        lastUpdated: 'Ostatnio updatowane',
        serviceWorker: {
          updatePopup: {
            message: "Dodano nowe treści.",
            buttonText: "Odśwież"
          }
        },
        nav: [
          {
            text: 'Wprowadzenie',
            link: '/pl/get-started/',
          },
          {
            text: 'Dalsze kroki',
            link: '/pl/advanced/'
          },
          {
            text: 'Przykłady',
            link: '/pl/examples/'
          },
          {
            text: 'API',
            link: '/pl/api/'
          },
          {
            text: 'Lista zmian - CHANGELOG',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/pl/get-started/': [
            {
              title: 'Wprowadzenie',
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
              title: 'Przykłady',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/pl/advanced/': [
            {
              title: 'Dalsze kroki',
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
              title: 'API',
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
      },
      '/es/': {
        label: 'Español',
        selectText: 'Lenguajes',
        editLinkText: 'Edita esta pagina en GitHub',
        lastUpdated: 'Ultima actualizacion',
        serviceWorker: {
          updatePopup: {
            message: "Nuevo contenido disponible.",
            buttonText: "Refresca"
          }
        },
        nav: [
          {
            text: 'Empezar',
            link: '/es/get-started/',
          },
          {
            text: 'Avanzado',
            link: '/es/advanced/'
          },
          {
            text: 'Ejemplos',
            link: '/es/examples/'
          },
          {
            text: 'APIs',
            link: '/es/api/'
          },
          {
            text: 'Cambios (changelog)',
            link: 'https://github.com/reactiverse/es4x/blob/master/CHANGELOG.md'
          },
        ],
        sidebar: {
          '/es/get-started/': [
            {
              title: 'Empezando',
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
          '/es/examples/': [
            {
              title: 'Ejemplos',
              collapsable: false,
              children: [
                '',
              ]
            }
          ],
          '/es/advanced/': [
            {
              title: 'Avanzado',
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
          '/es/api/': [
            {
              title: 'APIs',
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
