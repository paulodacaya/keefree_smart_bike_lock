package com.paulodacaya.keefree.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulodacaya.keefree.R;
import com.paulodacaya.keefree.utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardingActivity extends AppCompatActivity {
  
  @BindView( R.id.onBoardingViewPager ) ViewPager mViewPager;
  @BindView( R.id.onBoardingSkipButton ) Button mSkipButton;
  @BindView( R.id.onBoardingNextButton ) Button mNextButton;
  @BindView( R.id.onBoardingFinishButton ) Button mFinishButton;
  @BindView( R.id.onBoardingIndicator1 ) ImageView mIndicator1;
  @BindView( R.id.onBoardingIndicator2 ) ImageView mIndicator2;
  @BindView( R.id.onBoardingIndicator3 ) ImageView mIndicator3;
  
  private int mCurrentPage;
  private ImageView[] mIndicators;
  
  private ViewPagerAdapter mViewPagerAdapter;
  
  private final int[] slideImages = new int[] {
          R.drawable.keefree_onboarding_one,
          R.drawable.keefree_onboarding_two,
          R.drawable.keefree_onboarding_three
  };
  
  private final String[] slideTitles = new String[] {
          getString( R.string.onboarding_connect_title ),
          getString( R.string.onboarding_security_title ),
          getString( R.string.onboarding_track_title )
  };
  
  private final String[] slideBodys = new String[] {
          getString( R.string.onboarding_connect_body ),
          getString( R.string.onboarding_security_body ),
          getString( R.string.onboarding_track_body )
  };
  
  
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_on_boarding );
    ButterKnife.bind( this );
    
    // set Transitions
    setupTransitions();
    
    // set Indicators
    setIndicators();
    updateIndicators( 0 );
    
    // Set up Adapter and View Pager
    mViewPagerAdapter = new ViewPagerAdapter( this );
    mViewPager.setAdapter( mViewPagerAdapter );
    mViewPager.addOnPageChangeListener( viewPagerListener );
    
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    
    finish();
    
  }
  
  private void setupTransitions() {
    
    Fade fade = new Fade();
    fade.excludeTarget( android.R.id.navigationBarBackground, true );
    fade.excludeTarget( android.R.id.statusBarBackground, true );
    fade.setDuration( 800l ); // 1 second
  
    getWindow().setEnterTransition( fade );
    getWindow().setExitTransition( null );
    getWindow().setReenterTransition( null );
    getWindow().setReturnTransition( null );
    
  }
  
  private void setIndicators() {
    
    mIndicators = new ImageView[] {
            mIndicator1,
            mIndicator2,
            mIndicator3
    };
    
  }
  
  @OnClick( R.id.onBoardingNextButton )
  public void goToNextPage() {
    
    mViewPager.setCurrentItem( mCurrentPage + 1 );
    
  }
  
  @OnClick( { R.id.onBoardingSkipButton, R.id.onBoardingFinishButton } )
  public void goToMainAcitivty() {
  
    Utilities.setSharedPreferenceIsFirstTimeUserToFalse( this );
    Intent intent = new Intent( this, MainActivity.class );
    startActivity( intent );
    
  }
  
  ViewPager.OnPageChangeListener viewPagerListener  = new ViewPager.OnPageChangeListener() {
  
    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
    
    }
  
    @Override
    public void onPageSelected( int position ) {
      
      mCurrentPage = position;
      updateIndicators( position );
      
      switch( position ) {
        
        case 0:
          mSkipButton.setVisibility( View.VISIBLE );
          mNextButton.setVisibility( View.VISIBLE );
          mFinishButton.setVisibility( View.INVISIBLE );
          break;
          
        case 1:
          mSkipButton.setVisibility( View.VISIBLE );
          mNextButton.setVisibility( View.VISIBLE );
          mFinishButton.setVisibility( View.INVISIBLE );
          break;
          
        case 2:
          mSkipButton.setVisibility( View.INVISIBLE );
          mNextButton.setVisibility( View.INVISIBLE );
          mFinishButton.setVisibility( View.VISIBLE );
          break;
          
        default:
            break;
            
      }
      
    }
  
    @Override
    public void onPageScrollStateChanged( int state ) {
    
    }
    
  };
  
  void updateIndicators( int position ) {
    
    for( int i = 0; i < mIndicators.length; i++ ) {
      
      mIndicators[i].setBackgroundResource( i == position ? R.drawable.ic_indicator_selected : R.drawable.ic_indicator_unselected );
      
    }
    
  }
  
  /**
   * View Pager Adapter
   */
  class ViewPagerAdapter extends PagerAdapter {
    
    Context mContext;
    LayoutInflater mLayoutInflater;
  
    public ViewPagerAdapter( Context context ) {
      mContext = context;
    }
  
    @Override
    public int getCount() {
      return slideTitles.length;
    }
  
    @Override
    public boolean isViewFromObject( @NonNull View view, @NonNull Object object ) {
      return view == object;
    }
    
    
    // Inflate items
    @NonNull
    @Override
    public Object instantiateItem( @NonNull ViewGroup container, int position ) {
      
      mLayoutInflater = (LayoutInflater) mContext.getSystemService( mContext.LAYOUT_INFLATER_SERVICE );
      View view = mLayoutInflater.inflate( R.layout.page_on_boarding, container, false );
      
      ImageView onBoardingImage =  view.findViewById( R.id.onBoardingImage );
      TextView onBoardingTitle = view.findViewById( R.id.onBoardingTitle );
      TextView onBoardingBody = view.findViewById( R.id.onBoardingBody );
      
      onBoardingImage.setImageResource( slideImages[position] );
      onBoardingTitle.setText( slideTitles[position] );
      onBoardingBody.setText( slideBodys[position] );
      
      container.addView( view );
      
      return view;
    }
  
    @Override
    public void destroyItem( @NonNull ViewGroup container, int position, @NonNull Object object ) {
      container.removeView( (ConstraintLayout) object );
    }
  }
  
  
}
