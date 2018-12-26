const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const jeet = require('jeet');
const nib = require('nib');

module.exports = {
  devtool: 'source-map',
  entry: [
    './src/index'
  ],
  output: {
    path: path.join(__dirname, 'dist'),
    filename: '[name].js'
  },
  plugins: [
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.CommonsChunkPlugin({
      name: 'vendors',
      minChunks(module, count) {
        return (
            module.resource &&
            module.resource.indexOf(path.resolve('node_modules')) === 0
        )
      }
    }),
    new webpack.LoaderOptionsPlugin({
      options: {
        stylus: {
          use: [jeet(), nib()]
        },
      }
    }),
    new HtmlWebpackPlugin({
      title: 'Boot React',
      template: path.join(__dirname, 'assets/index-template.html')
    }),
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: JSON.stringify('production')
      }
    }),
    new webpack.optimize.UglifyJsPlugin({
      compressor: {
        warnings: false
      }
    }),
  ],
  resolve: {
    extensions: ['', '.js'],
    root: path.join(__dirname, 'src')
  },
  module: {
    rules: [{
      test: /\.js$/,
      loaders: ['babel-loader'],
      include: path.join(__dirname, 'src')
    }, {
      test: /\.json/,
      loaders: ['json-loader']
    }]
  }
};
