const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

distFolder = path.resolve(__dirname, 'dashboard')

module.exports = {
    mode: 'development',
    devtool: 'inline-source-map',
    entry: './src/index.js',
    devServer: {
        contentBase: distFolder
    },
    output: {
        path: distFolder,
        filename: 'js/dashboard.js'
    },
    module: {
        rules: [{
            test: /\.scss$/,
            use: [
                MiniCssExtractPlugin.loader,
                {
                    loader: 'css-loader'
                },
                {
                    loader: 'sass-loader',
                    options: {
                        sourceMap: true,
                        // options...
                    }
                }
            ]
        }]
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: 'css/dashboard.css'
        }),
    ]
};