/**
 * 文档 task
 *
 * @author 85ido-fe-generator
 */
import del from 'del';
import browserSync from 'browser-sync';

module.exports = function(gulp, common) {
    const { config, plugins: $ } = common;

    const bs = browserSync.create();
    gulp.task('docs:clean', () => {
        return del(['docs/**']);
    });

    gulp.task('docs:gen', () => {
        gulp.src(config.client.js.src)
            .pipe($.ngdocs.process({
                html5Mode: false,
                title: '前端组件文档',
                editExample: false
            }))
            .on('error', function(e) {
                $.util.log($.util.colors.green([
                    '-----------generating docs error-----------\n',
                    e.name,
                    ': ',
                    e.message,
                    '\n',
                    e.codeFrame,
                    '\n',
                    '-----------generating docs error-----------'
                ].join('')));
                this.emit('end');
            })
            .pipe(gulp.dest('./docs'))
            .pipe(bs.reload({ stream: true }));
    });

    gulp.task('docs:watch', ['docs:gen'], () => {
        bs.init({
            server: './docs',
            reloadDelay: 1.5e3
        });

        gulp.watch(config.client.js.src, [ 'docs:gen' ]);
    });
}
