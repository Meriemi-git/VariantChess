package fr.aboucorp.variantchess.app.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class PieceIconFactory {
    public static Drawable getDrawableForPieceID(Context ctx, PieceId pieceId) {
        switch (pieceId) {
            case BP1:
            case BP2:
            case BP3:
            case BP4:
            case BP5:
            case BP6:
            case BP7:
            case BP8:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_bp);
            case BK:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_bk);
            case BQ:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_bq);
            case BRN:
            case BLN:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_bn);
            case BRR:
            case BLR:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_br);
            case BLB:
            case BRB:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_bb);
            case WP1:
            case WP2:
            case WP3:
            case WP4:
            case WP5:
            case WP6:
            case WP7:
            case WP8:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wp);
            case WK:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wk);
            case WQ:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wq);
            case WRN:
            case WLN:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wn);
            case WRR:
            case WLR:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wr);
            case WLB:
            case WRB:
                return ContextCompat.getDrawable(ctx, R.drawable.ic_chess_wb);
            default:
                return null;
        }
    }
}
