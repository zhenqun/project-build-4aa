/**
 * 获取插件集合和路径配置
 *
 * @author 85ido-fe-generator
 */
import gulp from 'gulp';
import gulpPlugins from 'gulp-load-plugins';
import packageInfo from '../package.json';
// gulp plugins with gulp-load-plugins
export const plugins = gulpPlugins();

export const config = packageInfo.projectConfig;

export const util = {
    /**
     * 将 options.src 的内容复制到 options.dist，没有内容则不生成目录
     *
     * @param {Object} options 配置对象。
     * @param {string|array.<string>} options.src 源内容
     * @param {string} options.dist 输出目录
     */
    copy({src, dist}) {
        return gulp.src(src)
            .pipe(gulp.dest(dist));
    }
};

export default {
    plugins,
    config,
    util
}
