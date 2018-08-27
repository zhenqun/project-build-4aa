/**
 * 前端开发 task
 *
 * @author 85ido-fe-generator
 */
module.exports = function(gulp, common) {
    const { config, plugins: $ } = common;
    /**
     * 验证并转换前端 js。
     */
    gulp.task('transpile', ['hint-js'],
        () => gulp.src(config.client.js.src)
                    .pipe($.plumber())
                    .pipe($.cached('transpile'))
                    .pipe($.babel())
                    .pipe($.ngAnnotate({
                        single_quotes: true
                    }))
                    .pipe(gulp.dest(config.client.js.develop.dist))
    );
}
