module.exports = {
    base:'/translate.github.io/',
    head: [
        [
            'link', // 设置 favicon.ico，注意图片放在 public 文件夹下
            { rel: 'icon', href: 'sql-translation.png' }
        ]
    ],
    title: '多功能SQL生成器',
    description: '多功能SQL生成器，支持生成create、insert、update语句',
    themeConfig: {
        logo: '/sql-translation.png',
        nav: [
            { text: '首页', link: '/' },
            { text: '快速入门', link: '/pages/' },
            { text: '项目', link: '/core/' },
            { text: '💖意见反馈', link: 'https://support.qq.com/products/435498' },
            { text: '🌏GitHub', link: 'https://github.com/pdxjie/sql-translate' },
            { text: '在线访问', link: 'http://www.json-sql.com'},
        ],
        sidebar:{
            '/pages/':[
                {
                    title: '快速入门',
                    collapsable:false,
                    children:[
                        '',
                        'background',
                        'brief'
                    ]
                },
                {
                    title: '核心功能',
                    collapsable:false,
                    children:[
                        'grammar',
                        'replace',
                        'complex'
                    ]
                },
                {
                    title: '留言',
                    collapsable:false,
                    children:[
                        'talk'
                    ]
                }
            ],
            '/core/':[
                {
                    title: '整体分析',
                    collapsable:false,
                    sidebarDepth: 3,
                    children:[
                        '',
                    ]
                }
            ]
        }
    }
}