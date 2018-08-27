import del from 'del';

/**
 * 清理任务
 *
 * @author 85ido-fe-generator
 */
module.exports = function(gulp, common) {
    const { config, plugins: $ } = common;
    /**
     * 清理发布目录，只保留 .git, .gitignore, ecosystem.json, node_modules
     */
    gulp.task('clean', () =>
        del(['dist/**', '!dist', '!dist/.git', '!dist/.gitignore', '!dist/ecosystem.json', '!dist/node_modules', '!dist/node_modules/**'])
    );

    /**
     * 清理发布出的 revision 文件
     */
    gulp.task('clean:rev', () =>
        del([`${config.client.rev.dist}**`])
    );
}
