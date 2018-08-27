import runSequence from 'run-sequence';

/**
 * 发布
 *
 * @author 85ido-fe-generator
 */
module.exports = function(gulp, common) {
    const { config, plugins: $, util } = common;
    /**
     * 综合发布，顺序执行所有任务，在任务执行完成后会生成dist目录，该目录可以直接发布
     */
    gulp.task('release', (cb) => {
        runSequence(
            'clean',
            // 'transpile',
            'remember-deps',
            [
                'release-resources',
                // 如果有除 bundles 以外的 css 需要处理，取消下一行的注释
                'release-css',
                'release-deps'
            ],
            'release-js',
            'release-views',
            'clean:rev',
            cb
        );
    });
}
