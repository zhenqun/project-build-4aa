/**
 * 前端 release task
 *
 * @author 85ido-fe-generator
 */
import path from 'path';
import through from 'through2';

/**
 * 将文件引用替换为相对路径并保存
 *
 * @param {Array.<string>} deps 保存相对路径的数组
 * @param {string} projectDir 项目绝对根路径
 * @param {string} originalFolder 原始目录，相对于 public
 * @param {string?} targetFolder 要替换成的目录
 */
const innerFilePath = (deps, projectDir, originalFolder, targetFolder = originalFolder) => {
    return through.obj((file, enc, cb) => {
        if (file.isNull()) {
            // 返回空文件
            cb(null, file);
            return;
        }
        const filePath = file.path.replace(
            new RegExp(`^${projectDir.replace(/\\/g, '\\\\')}(static[\\\\\\\\/])${originalFolder}`),
            `!./$1${targetFolder}`
        );
        deps.push(filePath);

        cb(null, file);
    });
};

module.exports = function(gulp, common) {
    const { config, plugins: $, util } = common;
    const deps = [];

    /**
     * 复制除 css/js 的资源到生成目录。
     */
    gulp.task('release-resources', () =>
        util.copy({
            src: config.client.resource.src,
            dist: config.client.resource.dist
        })
    );

    /**
     * minify css file, exclude bundled
     */
    gulp.task('release-css', () =>
        gulp.src(Array.of(config.client.css.src, ...deps))
            .pipe($.plumber())
            .pipe($.cssnano({
                zindex: false
            }))
            .pipe($.rev())
            .pipe(gulp.dest(config.client.css.dist))
            .pipe($.rev.manifest('rev-css.json'))
            .pipe(gulp.dest(config.client.rev.dist))
    );

    /**
     * transpile js, generate sourcemaps
     */
    gulp.task('release-js', () =>
        gulp.src(Array.of(...config.client.js.src, ...deps))
            .pipe($.plumber())
            //.pipe($.stripDebug())
            .pipe($.sourcemaps.init())
            .pipe($.babel())
            .pipe($.ngAnnotate({
                single_quotes: true
            }))
            .pipe($.uglify())
            .pipe($.sourcemaps.write(`.`, {
                includeContent: false
            }))
            .pipe($.if('*.js', $.rev()))
            .pipe(gulp.dest(config.client.js.dist))
            .pipe($.rev.manifest('rev-js.json'))
            .pipe(gulp.dest(config.client.rev.dist))
    );

    /**
     * combine bundles js and bundles css
     */
    gulp.task('release-deps', () => {
        gulp.src(config.client.js.deps.src, {
            base: 'static/js/deps'
        })
        .pipe(gulp.dest(config.client.js.deps.dist));

        return gulp.src(config.client.js.deps.bundles)
            .pipe($.plumber())
            .pipe($.useref())
            .pipe($.if('*.css', $.cssnano({
                zindex: false
            })))
            .pipe($.if('*.css', $.rev()))
            .pipe($.if('*.js', $.ngAnnotate({
                single_quotes: true
            })))
            // If you running `release-deps` very slowly, try to comment uglify.
            .pipe($.if('*.js', $.uglify({
                compress: {
                    sequences: 20
                },
                mangle: true
            })))
            .pipe($.if('*.js', $.rev()))
            .pipe($.revReplace())
            // dest css/js resources to public/ others to views/
            .pipe($.if('**/+(css|js)/**/*', gulp.dest('dist/static/'), gulp.dest(config.server.views.dist)))
            .pipe($.rev.manifest('rev-deps.json'))
            .pipe(gulp.dest(config.client.rev.dist));
    });

    /**
     * 记住依赖中包含的文件，在 release-js, release-css 时会被排除
     */
    gulp.task('remember-deps', () => {
        const projectDir = path.resolve(__dirname, '..', '..', '..') + path.sep;

        return gulp.src(config.client.js.deps.bundles, { base: 'static/' })
            .pipe($.plumber())
            .pipe($.fileAssets({
                includeSrc: true
            }))
            .pipe($.if('*.js', innerFilePath(deps, projectDir, 'js')))
            .pipe($.if('*.css', innerFilePath(deps, projectDir, 'css')));
    });
}
