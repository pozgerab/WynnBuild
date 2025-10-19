package com.gertoxq.wynnbuild.webquery;

public class QueryData {

    /*
        '((?:\d+\.){3}\d+)'\,?(?=\s*\]) : Matches the last wynnbuilder version string in a string of js list like:
        const wynn_version_names = [
    '2.0.1.1',
    '2.0.1.2',
    '2.0.2.1',
    '2.0.2.3',
    '2.0.3.1',
    '2.0.4.1',
    '2.0.4.3',
    '2.0.4.4',
    '2.1.0.0',
    '2.1.0.1',
    '2.1.1.0',
    '2.1.1.1',
    '2.1.1.2',
    '2.1.1.3',
    '2.1.1.4',
    '2.1.1.5',
    '2.1.1.6',
    '2.1.1.7',
    '2.1.2.0',
    '2.1.3.0',
    '2.1.3.4',
    '2.1.4.0',
    '2.1.5.0',
];
    1 st group -> 2.1.5.0

    Note: this is used to gather data folder name.
            The version is extracted from url: https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/js/load_item.js
            The url contents change on every new version of wynnbuilder, so should be automatic
     */
}
