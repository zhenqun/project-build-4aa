import gulp from 'gulp';

import common, { config, plugins as $ } from './workflow/common';

const { client, server } = config;

$.loadSubtasks('workflow/tasks/**/*.js', common);
/**
 * watch transpile。
 */
gulp.task('watch', [ 'transpile' ], () => {
    gulp.watch(client.js.src, [ 'transpile' ]);
});

gulp.task('default', [ 'release' ]);
