//
//  Footer.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/23/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"

@interface Footer : NSObject

@property CCSprite* spr_1;
@property CCSprite* spr_2;
@property CCScene* scene;

-(id)initWithScene:(CCScene*)pScene;
-(void) update:(CCTime)delta;

@end
