/**
 * server 验证 task
 *
 * @author 85ido-fe-generator
 */
module.exports = function(gulp, common) {
    const { config, plugins: $ } = common;
    /**
     * 验证 server js。
     */
    gulp.task('hint-server', () =>
        gulp.src(config.server.js.src)
            .pipe($.plumber())
            .pipe($.eslint())
            .pipe($.eslint.format())
    );
}
