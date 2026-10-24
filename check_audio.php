<?php
/**
 * check_audio.php — بررسی وجود فایل‌های صوتی روی سرور
 *
 * ساختار انتظاری روی دیسک (مطابق URL کاتلین):
 *   {DOCROOT}/{qari}/{surahStr}/{surahStr}{verseStr}.mp3
 *   مثال: {DOCROOT}/sh/001/001001.mp3
 *
 * نکته: پیشوند "audio/" فقط در کش محلی اپ است و در URL سرور وجود ندارد.
 *        (اگر فایل‌ها در پوشه audio/ هستند، از ?base=/path/to/audio استفاده کنید)
 */

$qaris       = ['sh', 'af', 'pa', 'mi'];
$allowedExt  = 'mp3';

$baseDir = isset($_GET['base'])
    ? rtrim($_GET['base'], '/\\')
    : __DIR__;

if (!is_dir($baseDir)) {
    $baseDir = __DIR__;
}

$debug = !empty($_GET['debug']);

// تعداد آیات استاندارد (مجموع = 6236)
$surahVerses = [
    1 => 7,   2 => 286, 3 => 200, 4 => 176, 5 => 120, 6 => 165, 7 => 206, 8 => 75,
    9 => 129, 10 => 109, 11 => 123, 12 => 111, 13 => 43, 14 => 52, 15 => 99, 16 => 128,
    17 => 111, 18 => 110, 19 => 98, 20 => 135, 21 => 112, 22 => 78, 23 => 118, 24 => 64,
    25 => 77, 26 => 227, 27 => 93, 28 => 88, 29 => 69, 30 => 60, 31 => 34, 32 => 30,
    33 => 73, 34 => 54, 35 => 45, 36 => 83, 37 => 182, 38 => 88, 39 => 75, 40 => 85,
    41 => 54, 42 => 53, 43 => 89, 44 => 59, 45 => 37, 46 => 35, 47 => 38, 48 => 29,
    49 => 18, 50 => 45, 51 => 60, 52 => 49, 53 => 62, 54 => 55, 55 => 78, 56 => 96,
    57 => 29, 58 => 22, 59 => 24, 60 => 13, 61 => 14, 62 => 11, 63 => 11, 64 => 18,
    65 => 12, 66 => 12, 67 => 30, 68 => 52, 69 => 52, 70 => 44, 71 => 28, 72 => 28,
    73 => 20, 74 => 56, 75 => 40, 76 => 31, 77 => 50, 78 => 40, 79 => 46, 80 => 42,
    81 => 29, 82 => 19, 83 => 36, 84 => 25, 85 => 22, 86 => 17, 87 => 19, 88 => 26,
    89 => 30, 90 => 20, 91 => 15, 92 => 21, 93 => 11, 94 => 8,  95 => 8,  96 => 19,
    97 => 5,  98 => 8,  99 => 8,  100 => 11, 101 => 11, 102 => 8, 103 => 3, 104 => 9,
    105 => 5, 106 => 4, 107 => 7, 108 => 3, 109 => 6, 110 => 3, 111 => 5, 112 => 4,
    113 => 5, 114 => 6,
];

$report       = [];
$totalMissing = 0;
$totalEmpty   = 0;

foreach ($qaris as $qari) {
    // اولویت ۱: مسیر اصلی {baseDir}/{qari}
    // اولویت ۲: مسیر {baseDir}/audio/{qari}  (فقط به‌عنوان fallback)
    $candidates = [
        $baseDir . DIRECTORY_SEPARATOR . $qari,
        $baseDir . DIRECTORY_SEPARATOR . 'audio' . DIRECTORY_SEPARATOR . $qari,
    ];

    $chosenDir = null;
    foreach ($candidates as $cand) {
        if (is_dir($cand)) { $chosenDir = $cand; break; }
    }

    $missing = [];
    $empty   = [];

    if ($chosenDir === null) {
        // هیچ پوشه‌ای برای این قاری پیدا نشد
        foreach ($surahVerses as $sid => $verses) {
            $ss = str_pad($sid, 3, '0', STR_PAD_LEFT);
            for ($v = 1; $v <= $verses; $v++) {
                $vs = str_pad($v, 3, '0', STR_PAD_LEFT);
                $missing[] = [
                    'surah' => $sid, 'verse' => $v,
                    'file'  => $ss . $vs . '.' . $allowedExt,
                    'path'  => '(هیچ‌کدام از مسیرها یافت نشد)',
                ];
            }
        }
    } else {
        foreach ($surahVerses as $sid => $verses) {
            $ss = str_pad($sid, 3, '0', STR_PAD_LEFT);
            $surahDir = $chosenDir . DIRECTORY_SEPARATOR . $ss;
            for ($v = 1; $v <= $verses; $v++) {
                $vs = str_pad($v, 3, '0', STR_PAD_LEFT);
                $f  = $ss . $vs . '.' . $allowedExt;
                $p  = $surahDir . DIRECTORY_SEPARATOR . $f;

                if (!file_exists($p)) {
                    $missing[] = [
                        'surah' => $sid, 'verse' => $v,
                        'file'  => $f, 'path' => $p,
                    ];
                } elseif (@filesize($p) === 0) {
                    $empty[] = [
                        'surah' => $sid, 'verse' => $v,
                        'file'  => $f, 'path' => $p,
                    ];
                }
            }
        }
    }

    $totalMissing += count($missing);
    $totalEmpty   += count($empty);

    $report[$qari] = [
        'chosen_dir' => $chosenDir,
        'candidates' => $candidates,
        'missing'    => $missing,
        'empty'      => $empty,
    ];
}
?>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
<meta charset="UTF-8">
<title>گزارش فایل‌های صوتی</title>
<style>
    body { font-family: Tahoma, sans-serif; background:#0f1115; color:#eee; padding:20px; }
    h1,h2 { color:#4CAF50; }
    .info { background:#181b21; padding:12px; border-radius:8px; margin-bottom:16px; line-height:1.9; }
    .info code { background:#2a2a2a; padding:2px 6px; border-radius:4px; color:#FFC107; }
    .warn { color:#FF5722; font-weight:bold; }
    .ok   { color:#4CAF50; font-weight:bold; }
    table { width:100%; border-collapse:collapse; margin:12px 0; }
    th,td { border:1px solid #333; padding:6px 8px; text-align:center; font-size:14px; }
    th { background:#1e1e1e; color:#FFC107; }
    tr:nth-child(even) { background:#181b21; }
    details { margin:12px 0; }
    summary { cursor:pointer; color:#FFC107; font-weight:bold; }
</style>
</head>
<body>

<h1>📋 گزارش بررسی فایل‌های صوتی قرآن</h1>

<div class="info">
    <strong>پوشهٔ پایهٔ بررسی:</strong> <code><?= htmlspecialchars($baseDir) ?></code><br>
    <strong>ساختار انتظاری:</strong> <code>{qari}/{surahStr}/{surahStr}{verseStr}.<?= $allowedExt ?></code><br>
    <small>مثال: <code><?= htmlspecialchars($baseDir) ?>/sh/001/001001.<?= $allowedExt ?></code></small><br>
    <small>تغییر مسیر: <code>check_audio.php?base=/مسیر/دلخواه</code> · اطلاعات تشخیصی: <code>?debug=1</code></small>
</div>

<?php if ($debug): ?>
<details open>
    <summary>🔧 اطلاعات تشخیصی</summary>
    <div class="info">
        <strong>__DIR__:</strong> <code><?= htmlspecialchars(__DIR__) ?></code><br>
        <strong>DOCUMENT_ROOT:</strong> <code><?= htmlspecialchars($_SERVER['DOCUMENT_ROOT'] ?? '-') ?></code><br>
        <strong>SCRIPT_FILENAME:</strong> <code><?= htmlspecialchars($_SERVER['SCRIPT_FILENAME'] ?? '-') ?></code><br>
        <strong>کاربر PHP:</strong> <code><?= htmlspecialchars(function_exists('posix_getpwuid') && function_exists('posix_geteuid') ? (posix_getpwuid(posix_geteuid())['name'] ?? '?') : get_current_user()) ?></code><br>
        <strong>محتوای پوشهٔ پایه:</strong>
        <code>
        <?php
            $items = @scandir($baseDir) ?: [];
            $items = array_diff($items, ['.', '..']);
            echo htmlspecialchars(implode(' | ', array_slice($items, 0, 30)));
            if (count($items) > 30) echo ' ... (' . count($items) . ' مورد)';
        ?>
        </code>
    </div>
</details>
<?php endif; ?>

<h2>📊 خلاصه وضعیت قاریان</h2>
<table>
    <thead>
        <tr>
            <th>قاری</th>
            <th>پوشهٔ استفاده‌شده</th>
            <th>کل انتظار</th>
            <th>موجود</th>
            <th>غایب</th>
            <th>۰ بایتی</th>
        </tr>
    </thead>
    <tbody>
    <?php foreach ($report as $q => $r):
        $total = array_sum($surahVerses);
        $miss  = count($r['missing']);
        $empty = count($r['empty']);
        $okCount = $total - $miss - $empty;
    ?>
        <tr>
            <td><strong><?= htmlspecialchars($q) ?></strong></td>
            <td>
                <?php if ($r['chosen_dir']): ?>
                    <code><?= htmlspecialchars($r['chosen_dir']) ?></code>
                <?php else: ?>
                    <span class="warn">پیدا نشد</span><br>
                    <small>تلاش: <?= htmlspecialchars(implode(' , ', $r['candidates'])) ?></small>
                <?php endif; ?>
            </td>
            <td><?= $total ?></td>
            <td><?= $okCount ?></td>
            <td><?= $miss  > 0 ? '<span class="warn">'.$miss.'</span>'  : '0' ?></td>
            <td><?= $empty > 0 ? '<span class="warn">'.$empty.'</span>' : '0' ?></td>
        </tr>
    <?php endforeach; ?>
        <tr style="background:#2a2a2a;font-weight:bold;">
            <td colspan="4">جمع کل</td>
            <td><span class="warn"><?= $totalMissing ?></span></td>
            <td><span class="warn"><?= $totalEmpty ?></span></td>
        </tr>
    </tbody>
</table>

<h2>❌ فایل‌های غایب (<?= $totalMissing ?>)</h2>
<?php if ($totalMissing === 0): ?>
    <p class="ok">✅ هیچ فایل غایبی یافت نشد.</p>
<?php else: ?>
    <?php foreach ($report as $q => $r): if (empty($r['missing'])) continue; ?>
        <details>
            <summary>قاری <strong><?= htmlspecialchars($q) ?></strong> — <?= count($r['missing']) ?> فایل غایب</summary>
            <table>
                <thead><tr><th>#</th><th>سوره</th><th>آیه</th><th>نام فایل</th><th>مسیر</th></tr></thead>
                <tbody>
                <?php $i=1; foreach ($r['missing'] as $f): ?>
                    <tr>
                        <td><?= $i++ ?></td>
                        <td><?= $f['surah'] ?></td>
                        <td><?= $f['verse'] ?></td>
                        <td><?= htmlspecialchars($f['file']) ?></td>
                        <td class="warn"><?= htmlspecialchars($f['path']) ?></td>
                    </tr>
                <?php endforeach; ?>
                </tbody>
            </table>
        </details>
    <?php endforeach; ?>
<?php endif; ?>

<h2>⚠️ فایل‌های ۰ بایتی (<?= $totalEmpty ?>)</h2>
<?php if ($totalEmpty === 0): ?>
    <p class="ok">✅ هیچ فایل ۰ بایتی یافت نشد.</p>
<?php else: ?>
    <?php foreach ($report as $q => $r): if (empty($r['empty'])) continue; ?>
        <details>
            <summary>قاری <strong><?= htmlspecialchars($q) ?></strong> — <?= count($r['empty']) ?> فایل ۰ بایتی</summary>
            <table>
                <thead><tr><th>#</th><th>سوره</th><th>آیه</th><th>نام فایل</th><th>مسیر</th></tr></thead>
                <tbody>
                <?php $i=1; foreach ($r['empty'] as $f): ?>
                    <tr>
                        <td><?= $i++ ?></td>
                        <td><?= $f['surah'] ?></td>
                        <td><?= $f['verse'] ?></td>
                        <td><?= htmlspecialchars($f['file']) ?></td>
                        <td class="warn"><?= htmlspecialchars($f['path']) ?></td>
                    </tr>
                <?php endforeach; ?>
                </tbody>
            </table>
        </details>
    <?php endforeach; ?>
<?php endif; ?>

</body>
</html>
